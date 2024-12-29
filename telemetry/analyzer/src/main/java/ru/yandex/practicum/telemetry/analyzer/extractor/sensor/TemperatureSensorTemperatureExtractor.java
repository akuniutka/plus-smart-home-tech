package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorTemperatureExtractor extends BaseTemperatureSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.TEMPERATURE;
    }

    @Override
    protected Object extractValue(final TemperatureSensorAvro data) {
        return data.getTemperatureC();
    }
}
