package ru.yandex.practicum.telemetry.collector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSenderConfig;

@Configuration
public class KafkaClientConfiguration {

    @Bean(destroyMethod = "close")
    @Lazy
    public KafkaSender kafkaSender(final KafkaSenderConfig config) {
        return new KafkaSender(config);
    }
}
