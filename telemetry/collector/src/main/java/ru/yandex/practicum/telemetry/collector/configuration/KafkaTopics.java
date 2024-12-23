package ru.yandex.practicum.telemetry.collector.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kafka.topics")
public record KafkaTopics(String hubs, String sensors) {

}
