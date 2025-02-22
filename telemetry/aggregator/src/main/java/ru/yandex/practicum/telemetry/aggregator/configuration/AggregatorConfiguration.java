package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;

import java.time.Clock;

@Configuration
public class AggregatorConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean(destroyMethod = "stop")
    public KafkaListener<SensorEventAvro> sensorEventListener(final EventListenerProperties config,
            final SnapshotAggregator aggregator) {
        return new KafkaListener<>(SensorEventAvro.getClassSchema(), config, aggregator::aggregate);
    }
}
