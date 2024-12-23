package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kafka.topics")
public record KafkaTopics(String snapshots) {

}
