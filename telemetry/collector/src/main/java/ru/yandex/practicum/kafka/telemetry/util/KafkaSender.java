package ru.yandex.practicum.kafka.telemetry.util;

import org.apache.avro.specific.SpecificRecordBase;

import java.time.Instant;

public interface KafkaSender {

    void send(String topic, String key, Instant timestamp, SpecificRecordBase message);
}
