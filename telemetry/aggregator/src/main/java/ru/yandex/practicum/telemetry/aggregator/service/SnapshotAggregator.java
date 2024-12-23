package ru.yandex.practicum.telemetry.aggregator.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SnapshotAggregator {

    void aggregate(SensorEventAvro event);
}
