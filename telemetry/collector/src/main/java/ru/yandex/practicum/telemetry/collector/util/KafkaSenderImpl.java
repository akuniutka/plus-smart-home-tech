package ru.yandex.practicum.telemetry.collector.util;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@Component
@Lazy
@Slf4j
public class KafkaSenderImpl implements KafkaSender {

    private final Producer<String, SpecificRecordBase> producer;

    public KafkaSenderImpl(@Value("${kafka.bootstrap-servers}") final String bootstrapServers) {
        log.info("Creating Kafka producer...");
        this.producer = createProducer(bootstrapServers);
        log.info("Kafka producer created for {}", bootstrapServers);
    }

    @Override
    public void send(final String topic, final String key, final Instant timestamp, final SpecificRecordBase message) {
        final Long timestampInMillis = timestamp != null ? timestamp.toEpochMilli() : null;
        final ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, null, timestampInMillis,
                key, message);
        producer.send(record);
    }

    @PreDestroy
    public void close() {
        log.info("Closing Kafka producer...");
        producer.flush();
        producer.close(Duration.ofMillis(100L));
        log.info("Kafka producer closed");
    }

    private Producer<String, SpecificRecordBase> createProducer(final String bootstrapServers) {
        final Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaProducer<>(config, new StringSerializer(), new GeneralAvroSerializer());
    }
}
