package ru.yandex.practicum.telemetry.analyzer.listener;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerProperties;

@ConfigurationProperties("kafka.snapshot-listener")
public class SnapshotListenerProperties extends KafkaListenerProperties {

}
