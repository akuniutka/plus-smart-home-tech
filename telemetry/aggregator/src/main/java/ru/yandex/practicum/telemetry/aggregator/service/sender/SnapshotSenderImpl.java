package ru.yandex.practicum.telemetry.aggregator.service.sender;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;

@Component
public class SnapshotSenderImpl implements SnapshotSender {

    private static final Logger log = LoggerFactory.getLogger(SnapshotSenderImpl.class);
    private static final Integer PARTITION_NOT_SET = null;
    private final String topic;
    private final KafkaTemplate<String, SensorSnapshotAvro> kafkaTemplate;

    public SnapshotSenderImpl(
            final KafkaTopics kafkaTopics,
            final KafkaTemplate<String, SensorSnapshotAvro> kafkaTemplate
    ) {
        this.topic = kafkaTopics.snapshots();
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(final SensorSnapshotAvro snapshot) {
        final ProducerRecord<String, SensorSnapshotAvro> record = new ProducerRecord<>(
                topic,
                PARTITION_NOT_SET,
                snapshot.getTimestamp().toEpochMilli(),
                snapshot.getHubId(),
                snapshot
        );
        kafkaTemplate.send(record);
        log.info("Sent snapshot to Kafka: hubId = {}, timestamp = {}", snapshot.getHubId(), snapshot.getTimestamp());
        log.debug("Snapshot = {}", snapshot);
    }
}
