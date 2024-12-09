package ru.yandex.practicum.kafka.telemetry.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CollectorConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
