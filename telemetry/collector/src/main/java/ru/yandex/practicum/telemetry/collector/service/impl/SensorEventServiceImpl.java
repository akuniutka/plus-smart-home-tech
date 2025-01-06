package ru.yandex.practicum.telemetry.collector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;

@Service
public class SensorEventServiceImpl implements SensorEventService {

    private static final Logger log = LoggerFactory.getLogger(SensorEventServiceImpl.class);
    private final String topic;
    private final KafkaSender kafkaSender;

    public SensorEventServiceImpl(final KafkaTopics kafkaTopics, final KafkaSender kafkaSender) {
        this.topic = kafkaTopics.sensors();
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void addToProcessingQueue(final SensorEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.info("Sent sensor event to Kafka: hubId = {}, sensorId = {}, timestamp = {}",
                event.getHubId(), event.getId(), event.getTimestamp());
        log.debug("Sensor event = {}", event);
    }
}
