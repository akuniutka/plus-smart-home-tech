package ru.yandex.practicum.telemetry.aggregator.listener;

import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;

@Component
public class SensorEventListener implements SmartLifecycle {

    private static int listenerCount;
    private final KafkaListener<SensorEventAvro> listener;
    private volatile boolean isRunning;
    private volatile Thread listenerThread;
    private volatile Runnable shutdownCallback;

    public SensorEventListener(final KafkaListenerConfig config, final SnapshotAggregator aggregator) {
        this.listener = new KafkaListener<>(SensorEventAvro.getClassSchema(), config, aggregator::aggregate);
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void start() {
        listenerThread = new Thread(listener, "kafka-listener-" + (++listenerCount));
        listenerThread.start();
        isRunning = true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void stop(@NonNull final Runnable callback) {
        this.shutdownCallback = callback;
        stop();
    }

    @Override
    public void stop() {
        if (isRunning) {
            listener.stop();
            try {
                listenerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Kafka listener shutdown interrupted", e);
            }
            isRunning = false;
        }
        final Runnable callback = shutdownCallback;
        if (callback != null) {
            callback.run();
        }
    }
}
