package ru.yandex.practicum.kafka.telemetry.service.sender.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.service.sender.HubEventSender;
import ru.yandex.practicum.kafka.telemetry.util.KafkaSender;

@Component
@Slf4j
public class HubEventSenderImpl implements HubEventSender {

    private final String topic;
    private final KafkaSender kafkaSender;

    public HubEventSenderImpl(@Value("${kafka.topics.hub}") final String topic, final KafkaSender kafkaSender) {
        this.topic = topic;
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void send(final HubEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.debug("Sent hub event to topic [{}]: {}", topic, event);
    }
}
