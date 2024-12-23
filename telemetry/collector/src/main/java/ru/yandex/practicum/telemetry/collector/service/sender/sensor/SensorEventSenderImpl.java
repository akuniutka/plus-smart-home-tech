package ru.yandex.practicum.telemetry.collector.service.sender.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.sender.SensorEventSender;

@Component
public class SensorEventSenderImpl implements SensorEventSender {

    private static final Logger log = LoggerFactory.getLogger(SensorEventSenderImpl.class);
    private final String topic;
    private final KafkaSender kafkaSender;

    public SensorEventSenderImpl(final KafkaTopics kafkaTopics, final KafkaSender kafkaSender) {
        this.topic = kafkaTopics.sensors();
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void send(final SensorEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.debug("Sent sensor event to topic [{}]: {}", topic, event);
    }
}
