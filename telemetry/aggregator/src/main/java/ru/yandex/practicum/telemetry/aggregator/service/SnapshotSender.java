package ru.yandex.practicum.telemetry.aggregator.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

public interface SnapshotSender {

    void send(SensorSnapshotAvro snapshot);
}
