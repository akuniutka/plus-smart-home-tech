package ru.yandex.practicum.telemetry.aggregator.repository.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class SnapshotRepositoryImpl implements SnapshotRepository {

    private final Map<String, SensorSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public void save(final SensorSnapshotAvro snapshot) {
        Objects.requireNonNull(snapshot, "Cannot save snapshot: is null");
        snapshots.put(snapshot.getHubId(), snapshot);
    }

    @Override
    public Optional<SensorSnapshotAvro> findByHubId(final String hubId) {
        return Optional.ofNullable(snapshots.get(hubId));
    }
}
