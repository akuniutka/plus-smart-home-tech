package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

public abstract class BaseClimateSensorValueExtractor extends BaseSensorValueExtractor<ClimateSensorAvro> {

    @Override
    public Class<? extends SpecificRecordBase> getDataType() {
        return ClimateSensorAvro.class;
    }

    @Override
    protected ClimateSensorAvro cast(final Object data) {
        return (ClimateSensorAvro) data;
    }
}
