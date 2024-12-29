package ru.yandex.practicum.telemetry.analyzer.handler;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

public interface SnapshotHandler {

    void handle(SensorSnapshotAvro snapshot);
}
