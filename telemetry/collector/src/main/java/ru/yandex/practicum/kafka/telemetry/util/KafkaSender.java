package ru.yandex.practicum.kafka.telemetry.util;

import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaSender {

    void send(String topic, SpecificRecordBase message);
}
