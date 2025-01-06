package ru.yandex.practicum.telemetry.collector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;

@Service
public class HubEventServiceImpl implements HubEventService {

    private static final Logger log = LoggerFactory.getLogger(HubEventServiceImpl.class);
    private final String topic;
    private final KafkaSender kafkaSender;

    public HubEventServiceImpl(final KafkaTopics kafkaTopics, final KafkaSender kafkaSender) {
        this.topic = kafkaTopics.hubs();
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void addToProcessingQueue(final HubEventAvro event) {
        kafkaSender.send(topic, event.getHubId(), event.getTimestamp(), event);
        log.info("Sent hub event to Kafka: hubId = {}, timestamp = {}, payload type = {}",
                event.getHubId(), event.getTimestamp(), event.getPayload().getClass().getSimpleName());
        log.debug("Hub event = {}", event);
    }
}
