package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Component
public class ClimateSensorHumidityExtractor extends BaseClimateSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.HUMIDITY;
    }

    @Override
    protected Object extractValue(final ClimateSensorAvro data) {
        return data.getHumidity();
    }
}
