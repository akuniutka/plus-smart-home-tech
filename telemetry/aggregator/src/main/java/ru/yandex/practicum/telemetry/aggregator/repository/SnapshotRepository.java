package ru.yandex.practicum.telemetry.aggregator.repository;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

import java.util.Optional;

public interface SnapshotRepository {

    void save(SensorSnapshotAvro snapshot);

    Optional<SensorSnapshotAvro> findByHubId(String hubId);
}
