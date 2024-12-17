package ru.yandex.practicum.telemetry.collector.service.sender.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.sender.SensorEventSender;
import ru.yandex.practicum.telemetry.collector.util.KafkaSender;

@Component
@Slf4j
public class SensorEventSenderImpl implements SensorEventSender {

    private final String topic;
    private final KafkaSender kafkaSender;

    public SensorEventSenderImpl(@Value("${kafka.topics.sensor}") final String topic, final KafkaSender kafkaSender) {
        this.topic = topic;
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void send(final SensorEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.debug("Sent sensor event to topic [{}]: {}", topic, event);
    }
}
