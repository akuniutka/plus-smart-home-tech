package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Component
public class ClimateSensorCo2LevelExtractor extends BaseClimateSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.CO2LEVEL;
    }

    @Override
    protected Object extractValue(final ClimateSensorAvro data) {
        return data.getCo2Level();
    }
}
