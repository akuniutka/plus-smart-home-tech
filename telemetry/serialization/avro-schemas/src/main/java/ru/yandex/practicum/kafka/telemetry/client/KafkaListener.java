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
    private volatile Consumer<String, T> consumer;
    private volatile Thread thread;
    private volatile Status status = Status.NEW;

    public KafkaListener(final Schema schema, final KafkaListenerProperties config,
            final java.util.function.Consumer<T> messageHandler) {
        this.schema = schema;
        this.config = config;
        this.messageHandler = messageHandler;
    }

    public synchronized void start() {
        if (status != Status.NEW) {
            throw new IllegalThreadStateException();
        }
        thread = new Thread(this::run);
        thread.setName("kafka-listener-" + (++listenerCount));
        status = Status.READY;
        thread.start();
    }

    public void stop() {
        if (status == Status.RUNNING) {
            status = Status.INTERRUPTED;
            consumer.wakeup();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Kafka listener shutdown interrupted", e);
            }
        }
    }

    private void run() {
        try {
            createConsumer(schema, new HashMap<>(config.getProperties()));
            subscribe(config.getTopics());
            if (config.getStrategy() == Strategy.AT_MOST_ONCE) {
                startAtMostOnceLoop();
            } else {
                startAtLeastOnceLoop();
            }
            // Listener quits loop when status becomes not RUNNING
            // (i.e. status equals INTERRUPTED).
        } catch (WakeupException ignored) {
            // Nothing to do here, close consumer in finally block.
            // Also, status equals becomes INTERRUPTED here.
        } catch (Exception e) {
            status = Status.INTERRUPTED;
            log.error(e.getMessage(), e);
        } finally {
            closeConsumer();
        }
    }

    private void createConsumer(final Schema schema, final Map<String, Object> properties) {
        log.info("Creating Kafka consumer...");
        consumer = new KafkaConsumer<>(properties, new StringDeserializer(), new BaseAvroDeserializer<>(schema));
        log.info("Kafka consumer created");
        status = Status.RUNNING;
    }

    private void subscribe(final List<String> topics) {
        consumer.subscribe(topics);
        log.info("Kafka consumer subscribed to topics {}", config.getTopics());
    }

    private void startAtMostOnceLoop() {
        while (status == Status.RUNNING) {
            ConsumerRecords<String, T> records = pollRecords();
            consumer.commitSync();
            for (ConsumerRecord<String, T> record : records) {
                messageHandler.accept(record.value());
            }
        }
    }

    private void startAtLeastOnceLoop() {
        while (status == Status.RUNNING) {
            ConsumerRecords<String, T> records = pollRecords();
            final int recordsTotal = records.count();
            int recordsProcessed = 0;
            for (ConsumerRecord<String, T> record : records) {
                messageHandler.accept(record.value());
                updateCurrentOffsets(record);
                recordsProcessed++;
                if (isCommitBatchProcessed(recordsProcessed) && recordsProcessed != recordsTotal) {
                    consumer.commitAsync(currentOffsets, offsetCommitCallback(recordsProcessed));
                    if (status != Status.RUNNING) {
                        return;
                    }
                }
            }
            consumer.commitAsync(offsetCommitCallback(recordsProcessed));
        }
    }

    private ConsumerRecords<String, T> pollRecords() {
        final ConsumerRecords<String, T> records = consumer.poll(config.getPollTimeout());
        log.debug("Received {} record(s) from Kafka broker", records.count());
        return records;
    }

    private void updateCurrentOffsets(final ConsumerRecord<String, T> record) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );
    }

    private boolean isCommitBatchProcessed(final int recordsProcessed) {
        return config.getCommitBatchSize() > 0 && recordsProcessed % config.getCommitBatchSize() == 0;
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
        if (consumer != null) {
            log.info("Closing Kafka consumer...");
            try {
                if (config.getStrategy() != Strategy.AT_MOST_ONCE) {
                    log.debug("Commiting processed message offsets...");
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                consumer.close();
                log.info("Kafka consumer closed");
            }
        }
    }

    public enum Strategy {
        AT_MOST_ONCE,
        AT_LEAST_ONCE
    }

    private enum Status {
        NEW,
        READY,
        RUNNING,
        INTERRUPTED
    }
}
