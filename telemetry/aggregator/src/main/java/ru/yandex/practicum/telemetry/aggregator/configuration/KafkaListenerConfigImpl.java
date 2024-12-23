package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerConfig;

import java.util.List;
import java.util.Properties;

@ConfigurationProperties("kafka.listener")
public record KafkaListenerConfigImpl(
        List<String> topics,
        long pollTimeout,
        int commitBatchSize,
        Properties properties
) implements KafkaListenerConfig {

}
