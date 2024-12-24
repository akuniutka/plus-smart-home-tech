package ru.yandex.practicum.telemetry.aggregator.listener;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AggregatorRunner implements ApplicationRunner {

    private final SensorEventListener listener;

    public AggregatorRunner(final SensorEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void run(final ApplicationArguments args) {
        listener.start();
    }
}
