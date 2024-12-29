package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

public abstract class BaseSwitchSensorValueExtractor extends BaseSensorValueExtractor<SwitchSensorAvro> {

    @Override
    public Class<? extends SpecificRecordBase> getDataType() {
        return SwitchSensorAvro.class;
    }

    @Override
    protected SwitchSensorAvro cast(final Object data) {
        return (SwitchSensorAvro) data;
    }
}
