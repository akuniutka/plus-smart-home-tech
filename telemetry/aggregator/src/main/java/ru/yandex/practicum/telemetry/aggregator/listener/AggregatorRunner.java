package ru.yandex.practicum.telemetry.aggregator.listener;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class AggregatorRunner implements ApplicationRunner {

    private final KafkaListener<SensorEventAvro> sensorEventListener;

    public AggregatorRunner(final KafkaListener<SensorEventAvro> sensorEventListener) {
        this.sensorEventListener = sensorEventListener;
    }

    @Override
    public void run(final ApplicationArguments args) {
        sensorEventListener.start();
    }
}
