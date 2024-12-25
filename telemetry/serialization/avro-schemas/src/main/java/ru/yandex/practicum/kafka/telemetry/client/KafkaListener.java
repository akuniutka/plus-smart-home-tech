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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaListener<T extends SpecificRecordBase> {

    private static final Logger log = LoggerFactory.getLogger(KafkaListener.class);
    private static int listenerCount;
    private final Schema schema;
    private final KafkaListenerProperties config;
    private final java.util.function.Consumer<T> messageHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private final Object lock = new Object();
    private volatile Consumer<String, T> consumer;
    private volatile Thread thread;
    private volatile Status status = Status.NEW;

    public KafkaListener(final Schema schema, final KafkaListenerProperties config,
            final java.util.function.Consumer<T> messageHandler) {
        this.schema = schema;
        this.config = config;
        this.messageHandler = messageHandler;
    }

    public void start() {
        synchronized (lock) {
            if (status != Status.NEW && status != Status.STOPPED) {
                throw new IllegalThreadStateException();
            }
            thread = new Thread(this::run);
            thread.setName("kafka-listener-" + (++listenerCount));
            status = Status.READY;
            thread.start();
        }
    }

    public void stop() {
        synchronized (lock) {
            if (status == Status.RUNNING) {
                status = Status.STOPPING;
                consumer.wakeup();
                try {
                    thread.join();
                    status = Status.STOPPED;
                } catch (InterruptedException e) {
                    status = Status.UNDEFINED;
                    throw new RuntimeException("Kafka listener shutdown interrupted", e);
                }
            }
        }
    }

    private void run() {
        try {
            createConsumer(schema, new HashMap<>(config.getProperties()));
            subscribe(config.getTopics());
            pollMessages();
        } catch (WakeupException ignored) {
            // Nothing to do here, close consumer in finally block
        } finally {
            closeConsumer();
        }
    }

    private void createConsumer(final Schema schema, final Map<String, Object> properties) {
        synchronized (lock) {
            status = Status.STARTING;
            log.info("Creating Kafka consumer...");
            consumer = new KafkaConsumer<>(properties, new StringDeserializer(),
                    new BaseAvroDeserializer<>(schema));
            log.info("Kafka consumer created");
            status = Status.RUNNING;
        }
    }

    private void subscribe(final List<String> topics) {
        consumer.subscribe(topics);
        log.info("Kafka consumer subscribed to topics {}", config.getTopics());
    }

    private void pollMessages() {
        while (status == Status.RUNNING) {
            ConsumerRecords<String, T> records = consumer.poll(config.getPollTimeout());
            log.debug("Received {} record(s) from Kafka broker", records.count());
            int recordProcessed = 0;
            for (ConsumerRecord<String, T> record : records) {
                if (status != Status.RUNNING) {
                    return;
                }
                messageHandler.accept(record.value());
                // TODO special case: if processed records == total records
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
        if (config.getCommitBatchSize() > 0 && recordProcessed % config.getCommitBatchSize() == 0) {
            consumer.commitAsync(currentOffsets, offsetCommitCallback(recordProcessed));
        }
    }

    private OffsetCommitCallback offsetCommitCallback(final int recordProcessed) {
        return (offsets, exception) -> {
            if (exception != null) {
                log.warn("Offset committing error: {}", offsets, exception);
            } else {
                log.debug("Processed {} record(s)", recordProcessed);
            }
        };
    }

    private void closeConsumer() {
        if (status == Status.STOPPING || status == Status.RUNNING) {
            log.info("Closing Kafka consumer...");
            try {
                log.debug("Commiting processed message offsets...");
                consumer.commitSync(currentOffsets);
            } finally {
                consumer.close();
                log.info("Kafka consumer closed");
            }
        } else if (status == Status.STARTING) {
            status = Status.UNDEFINED;
        }
    }

    private enum Status {
        NEW,
        READY,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED,
        UNDEFINED
    }
}
