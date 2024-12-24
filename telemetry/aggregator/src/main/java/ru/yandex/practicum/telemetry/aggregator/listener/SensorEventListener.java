package ru.yandex.practicum.telemetry.aggregator.listener;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;

@Component
public class SensorEventListener implements DisposableBean {

    private static int listenerCount;
    private final KafkaListener<SensorEventAvro> listener;
    private volatile Thread listenerThread;

    public SensorEventListener(final KafkaListenerConfig config, final SnapshotAggregator aggregator) {
        this.listener = new KafkaListener<>(SensorEventAvro.getClassSchema(), config, aggregator::aggregate);
    }

    public void start() {
        if (listenerThread != null) {
            throw new IllegalThreadStateException("Cannot start listener twice");
        }
        listenerThread = new Thread(listener, "kafka-listener-" + (++listenerCount));
        listenerThread.start();
    }

    @Override
    public void destroy() {
        listener.stop();
        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Kafka listener shutdown interrupted", e);
        }
    }
}
