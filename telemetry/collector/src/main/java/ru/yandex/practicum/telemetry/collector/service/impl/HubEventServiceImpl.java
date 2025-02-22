package ru.yandex.practicum.telemetry.collector.service.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;

@Service
public class HubEventServiceImpl implements HubEventService {

    private static final Logger log = LoggerFactory.getLogger(HubEventServiceImpl.class);
    private static final Integer PARTITION_NOT_SET = null;
    private final String topic;
    private final KafkaTemplate<String, HubEventAvro> kafkaTemplate;

    public HubEventServiceImpl(
            final KafkaTopics kafkaTopics,
            final KafkaTemplate<String, HubEventAvro> kafkaTemplate
    ) {
        this.topic = kafkaTopics.hubs();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void addToProcessingQueue(final HubEventAvro event) {
        final ProducerRecord<String, HubEventAvro> record = new ProducerRecord<>(
                topic,
                PARTITION_NOT_SET,
                event.getTimestamp().toEpochMilli(),
                event.getHubId(),
                event
        );
        kafkaTemplate.send(record);
        log.info("Sent hub event to Kafka: hubId = {}, timestamp = {}, payload type = {}",
                event.getHubId(), event.getTimestamp(), event.getPayload().getClass().getSimpleName());
        log.debug("Hub event = {}", event);
    }
}
