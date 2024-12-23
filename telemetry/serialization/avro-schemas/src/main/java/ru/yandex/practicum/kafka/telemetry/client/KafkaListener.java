package ru.yandex.practicum.kafka.telemetry.client;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.serialization.BaseAvroDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaListener<T extends SpecificRecordBase> implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(KafkaListener.class);
    private final Schema schema;
    private final KafkaListenerConfig config;
    private final Duration pollTimeout;
    private final java.util.function.Consumer<T> messageHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private volatile Consumer<String, T> consumer;
    private volatile boolean isStopped;

    public KafkaListener(final Schema schema, final KafkaListenerConfig config,
            final java.util.function.Consumer<T> messageHandler) {
        this.schema = schema;
        this.config = config;
        this.pollTimeout = Duration.ofMillis(config.pollTimeout());
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        try {
            consumer = createConsumer(schema, config.properties());
            consumer.subscribe(config.topics());
            log.info("Kafka consumer subscribed to topics {}", config.topics());
            runPollLoop();
        } catch (WakeupException ignored) {
            // Nothing to do here, close consumer in finally block
        } finally {
            log.info("Closing Kafka consumer...");
            try {
                log.debug("Fixing processed message offsets...");
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                log.info("Kafka consumer closed");
            }
        }
    }

    public void stop() {
        isStopped = true;
        consumer.wakeup();
    }

    private Consumer<String, T> createConsumer(final Schema schema, final Properties properties) {
        log.info("Creating Kafka consumer...");
        final Consumer<String, T> consumer = new KafkaConsumer<>(properties, new StringDeserializer(),
                new BaseAvroDeserializer<>(schema));
        log.info("Kafka consumer created");
        return consumer;
    }

    private void runPollLoop() {
        while (!isStopped) {
            ConsumerRecords<String, T> records = consumer.poll(pollTimeout);
            log.debug("Received {} record(s) from Kafka broker", records.count());
            int recordProcessed = 0;
            for (ConsumerRecord<String, T> record : records) {
                if (isStopped) {
                    return;
                }
                messageHandler.accept(record.value());
                updateCurrentOffsets(record, ++recordProcessed);
            }
            consumer.commitAsync(offsetCommitCallback(recordProcessed));
        }
    }

    private void updateCurrentOffsets(final ConsumerRecord<String, T> record, final int recordProcessed) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
        if (config.commitBatchSize() > 0 && recordProcessed % config.commitBatchSize() == 0) {
            consumer.commitAsync(currentOffsets, offsetCommitCallback(recordProcessed));
        }
    }

    private OffsetCommitCallback offsetCommitCallback(final int recordProcessed) {
        return (offsets, exception) -> {
            if (exception != null) {
                log.warn("Offset fixing error: {}", offsets, exception);
            } else {
                log.debug("Processed {} record(s)", recordProcessed);
            }
        };
    }
}
