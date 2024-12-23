package ru.yandex.practicum.kafka.telemetry.client;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.serialization.GeneralAvroSerializer;

import java.time.Instant;
import java.util.Properties;

public class KafkaSender {

    private static final Logger log = LoggerFactory.getLogger(KafkaSender.class);
    private final Producer<String, SpecificRecordBase> producer;

    public KafkaSender(final KafkaSenderConfig config) {
        log.debug("{}", config.properties());
        this.producer = createProducer(config.properties());
    }

    public void send(final String topic, final String key, final Instant timestamp, final SpecificRecordBase message) {
        final Long timestampInMillis = timestamp != null ? timestamp.toEpochMilli() : null;
        final ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, timestampInMillis,
                key, message);
        producer.send(record);
    }

    public void close() {
        log.info("Closing Kafka producer...");
        try {
            log.debug("Flushing buffered data from Kafka producer to Kafka broker...");
            producer.flush();
        } finally {
            producer.close();
            log.info("Kafka producer closed");
        }
    }

    private Producer<String, SpecificRecordBase> createProducer(final Properties properties) {
        log.info("Creating Kafka producer...");
        final Producer<String, SpecificRecordBase> producer = new KafkaProducer<>(properties, new StringSerializer(),
                new GeneralAvroSerializer());
        log.info("Kafka producer created");
        return producer;
    }
}
