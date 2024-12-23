package ru.yandex.practicum.telemetry.aggregator.service.aggregator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class SnapshotAggregatorImpl implements SnapshotAggregator {

    private final Map<String, SensorSnapshotAvro> snapshots = new HashMap<>();
    private final SnapshotSender sender;

    public SnapshotAggregatorImpl(final SnapshotSender sender) {
        this.sender = sender;
    }

    @Override
    public void aggregate(final SensorEventAvro event) {
        final SensorSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), this::newSnapshot);
        final SensorStateAvro currentState = snapshot.getSensorsState().get(event.getId());
        if (!containsNewData(event, currentState)) {
            return;
        }
        snapshot.getSensorsState().put(event.getId(), mapToSensorState(event));
        snapshot.setTimestamp(Instant.now());
        sender.send(snapshot);
    }

    private SensorSnapshotAvro newSnapshot(final String hubId) {
        return SensorSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(Instant.now())
                .setSensorsState(new HashMap<>())
                .build();
    }

    private boolean containsNewData(final SensorEventAvro event, final SensorStateAvro currentState) {
        if (currentState == null) {
            return true;
        }
        return currentState.getTimestamp().isBefore(event.getTimestamp())
                && !event.getPayload().equals(currentState.getData());
    }

    private SensorStateAvro mapToSensorState(final SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
