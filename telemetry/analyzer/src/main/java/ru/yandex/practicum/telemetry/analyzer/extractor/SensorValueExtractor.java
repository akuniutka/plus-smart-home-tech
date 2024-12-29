package ru.yandex.practicum.telemetry.analyzer.extractor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public interface SensorValueExtractor {

    Class<? extends SpecificRecordBase> getDataType();

    ConditionTypeAvro getMetricType();

    Object extractValue(Object data);
}
