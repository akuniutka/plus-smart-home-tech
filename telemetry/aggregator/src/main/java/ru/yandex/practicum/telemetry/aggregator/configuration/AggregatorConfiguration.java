package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AggregatorConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
