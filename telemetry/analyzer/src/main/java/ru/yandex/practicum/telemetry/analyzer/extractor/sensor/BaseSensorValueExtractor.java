package ru.yandex.practicum.telemetry.analyzer.extractor.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.telemetry.analyzer.extractor.SensorValueExtractor;

public abstract class BaseSensorValueExtractor<T extends SpecificRecordBase> implements SensorValueExtractor {

    @Override
    public Object extractValue(final Object data) {
        if (data.getClass() != getDataType()) {
            throw new IllegalArgumentException("Unknown sensor data type: " + data.getClass());
        }
        final T _data = cast(data);
        return extractValue(_data);
    }

    protected abstract T cast(Object data);

    protected abstract Object extractValue(T data);
}
