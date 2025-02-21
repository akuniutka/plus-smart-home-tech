package ru.yandex.practicum.telemetry.collector.service.impl;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;

@Service
public class SensorEventServiceImpl implements SensorEventService {

    private static final Logger log = LoggerFactory.getLogger(SensorEventServiceImpl.class);
    private static final Integer PARTITION_NOT_SET = null;
    private final String topic;
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public SensorEventServiceImpl(
            final KafkaTopics kafkaTopics,
            final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate
    ) {
        this.topic = kafkaTopics.sensors();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void addToProcessingQueue(final SensorEventAvro event) {
        final ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                PARTITION_NOT_SET,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                event
        );
        kafkaTemplate.send(record);
        log.info("Sent sensor event to Kafka: hubId = {}, sensorId = {}, timestamp = {}",
                event.getHubId(), event.getId(), event.getTimestamp());
        log.debug("Sensor event = {}", event);
    }
}
