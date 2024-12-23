package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSenderConfig;

import java.util.Properties;

@ConfigurationProperties("kafka.sender")
public record KafkaSenderConfigImpl(Properties properties) implements KafkaSenderConfig {

}
