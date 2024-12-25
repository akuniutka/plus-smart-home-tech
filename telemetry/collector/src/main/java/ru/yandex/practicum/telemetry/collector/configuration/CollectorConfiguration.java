package ru.yandex.practicum.telemetry.collector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSenderConfig;

@Configuration
public class CollectorConfiguration {

    @Bean(destroyMethod = "close")
    public KafkaSender kafkaSender(final KafkaSenderConfig config) {
        return new KafkaSender(config);
    }
}
