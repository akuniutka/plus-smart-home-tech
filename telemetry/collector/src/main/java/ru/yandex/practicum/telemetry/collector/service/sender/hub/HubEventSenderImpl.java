package ru.yandex.practicum.telemetry.collector.service.sender.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.sender.HubEventSender;

@Component
public class HubEventSenderImpl implements HubEventSender {

    private static final Logger log = LoggerFactory.getLogger(HubEventSenderImpl.class);
    private final String topic;
    private final KafkaSender kafkaSender;

    public HubEventSenderImpl(final KafkaTopics kafkaTopics, final KafkaSender kafkaSender) {
        this.topic = kafkaTopics.hubs();
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void send(final HubEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.debug("Sent hub event to topic [{}]: {}", topic, event);
    }
}
