package ru.yandex.practicum.kafka.telemetry.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class KafkaSenderImpl implements KafkaSender {

    private final Producer<String, SpecificRecordBase> producer;

    public KafkaSenderImpl(final String bootstrapServers) {
        log.info("Creating Kafka producer...");
        this.producer = createProducer(bootstrapServers);
        log.info("Kafka producer created for {}", bootstrapServers);
    }

    @Override
    public void send(final String topic, final SpecificRecordBase message) {
        final ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, message);
        producer.send(record);
    }

    private Producer<String, SpecificRecordBase> createProducer(final String bootstrapServers) {
        final Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaProducer<>(config, new StringSerializer(), new GeneralAvroSerializer());
    }
}
