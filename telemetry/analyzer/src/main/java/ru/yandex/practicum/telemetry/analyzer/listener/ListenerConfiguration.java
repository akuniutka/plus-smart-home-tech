package ru.yandex.practicum.telemetry.analyzer.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.dispatcher.HubEventDispatcher;
import ru.yandex.practicum.telemetry.analyzer.handler.SnapshotHandler;

@Configuration
public class ListenerConfiguration {

    @Bean(destroyMethod = "stop")
    public KafkaListener<HubEventAvro> hubEventListener(final HubEventListenerProperties config,
            final HubEventDispatcher hubEventDispatcher) {
        return new KafkaListener<>(HubEventAvro.getClassSchema(), config, hubEventDispatcher::dispatch);
    }

    @Bean(destroyMethod = "stop")
    public KafkaListener<SensorSnapshotAvro> snapshotListener(final SnapshotListenerProperties config,
            final SnapshotHandler snapshotHandler) {
        return new KafkaListener<>(SensorSnapshotAvro.getClassSchema(), config, snapshotHandler::handle);
    }
}
