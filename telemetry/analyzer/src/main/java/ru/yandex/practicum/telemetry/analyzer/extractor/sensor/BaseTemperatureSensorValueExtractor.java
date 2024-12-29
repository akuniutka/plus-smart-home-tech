package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

public abstract class BaseTemperatureSensorValueExtractor extends BaseSensorValueExtractor<TemperatureSensorAvro> {

    @Override
    public Class<? extends SpecificRecordBase> getDataType() {
        return TemperatureSensorAvro.class;
    }

    @Override
    protected TemperatureSensorAvro cast(final Object data) {
        return (TemperatureSensorAvro) data;
    }
}
