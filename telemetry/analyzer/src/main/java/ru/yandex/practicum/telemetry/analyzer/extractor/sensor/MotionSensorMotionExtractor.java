package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorMotionExtractor extends BaseMotionSensorValueExtractor {

    @Override
    public ConditionTypeAvro getMetricType() {
        return ConditionTypeAvro.MOTION;
    }

    @Override
    protected Object extractValue(final MotionSensorAvro data) {
        return data.getMotion();
    }
}
