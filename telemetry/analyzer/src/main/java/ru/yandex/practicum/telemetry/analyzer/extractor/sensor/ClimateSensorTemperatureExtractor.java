package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Component
public class ClimateSensorTemperatureExtractor extends BaseClimateSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.TEMPERATURE;
    }

    @Override
    protected Object extractValue(final ClimateSensorAvro data) {
        return data.getTemperatureC();
    }
}
