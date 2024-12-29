package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorLuminosityExtractor extends BaseLightSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.LUMINOSITY;
    }

    @Override
    protected Object extractValue(final LightSensorAvro data) {
        return data.getLuminosity();
    }
}
