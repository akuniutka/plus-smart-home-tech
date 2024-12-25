package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSenderConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;

@Configuration
public class AggregatorConfiguration {

    @Bean(destroyMethod = "stop")
    public KafkaListener<SensorEventAvro> sensorEventListener(final EventListenerProperties config,
            final SnapshotAggregator aggregator) {
        return new KafkaListener<>(SensorEventAvro.getClassSchema(), config, aggregator::aggregate);
    }

    @Bean(destroyMethod = "close")
    public KafkaSender kafkaSender(final KafkaSenderConfig config) {
        return new KafkaSender(config);
    }
}
