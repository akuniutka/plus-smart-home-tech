package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import java.util.function.Supplier;

public interface SensorValueExtractorFactory {

    Supplier<Object> getExtractor(Object sensorData, ConditionTypeAvro metric);
}
