package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

public abstract class BaseMotionSensorValueExtractor extends BaseSensorValueExtractor<MotionSensorAvro> {

    @Override
    public Class<? extends SpecificRecordBase> getDataType() {
        return MotionSensorAvro.class;
    }

    @Override
    protected MotionSensorAvro cast(final Object data) {
        return (MotionSensorAvro) data;
    }
}
