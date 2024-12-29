package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

public abstract class BaseLightSensorValueExtractor extends BaseSensorValueExtractor<LightSensorAvro> {

    @Override
    protected LightSensorAvro cast(final Object data) {
        return (LightSensorAvro) data;
    }

    @Override
    public Class<? extends SpecificRecordBase> getDataType() {
        return LightSensorAvro.class;
    }
}
