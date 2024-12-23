package ru.yandex.practicum.telemetry.aggregator.service.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;

@Component
public class SnapshotSenderImpl implements SnapshotSender {

    private static final Logger log = LoggerFactory.getLogger(SnapshotSenderImpl.class);
    private final String topic;
    private final KafkaSender kafkaSender;

    public SnapshotSenderImpl(final KafkaTopics kafkaTopics, final KafkaSender kafkaSender) {
        this.topic = kafkaTopics.snapshots();
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void send(final SensorSnapshotAvro snapshot) {
        kafkaSender.send(topic, snapshot.getHubId(), snapshot.getTimestamp(), snapshot);
        log.debug("Sent sensor snapshot to topic [{}]: {}", topic, snapshot);
    }
}
