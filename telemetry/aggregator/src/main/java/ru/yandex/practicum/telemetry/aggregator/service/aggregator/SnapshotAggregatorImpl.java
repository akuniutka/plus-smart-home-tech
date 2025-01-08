package ru.yandex.practicum.telemetry.aggregator.service.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotRepository;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;

@Component
public class SnapshotAggregatorImpl implements SnapshotAggregator {

    private static final Logger log = LoggerFactory.getLogger(SnapshotAggregatorImpl.class);
    private final Clock clock;
    private final SnapshotRepository repository;
    private final SnapshotSender sender;

    public SnapshotAggregatorImpl(final Clock clock, final SnapshotRepository repository, final SnapshotSender sender) {
        this.clock = clock;
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    public void aggregate(final SensorEventAvro event) {
        log.info("Received event: hubId = {}, sensorId = {}, timestamp = {}", event.getHubId(), event.getId(),
                event.getTimestamp());
        log.debug("Event = {}", event);
        final SensorSnapshotAvro snapshot = repository.findByHubId(event.getHubId())
                .orElseGet(() -> newSnapshot(event.getHubId()));
        final SensorStateAvro currentState = snapshot.getSensorsState().get(event.getId());
        if (!containsNewData(event, currentState)) {
            log.info("Event skipped because contains no new data: hubId = {}, sensorId = {}, timestamp = {}",
                    event.getHubId(), event.getId(), event.getTimestamp());
            log.debug("Event = {}", event);
            return;
        }
        snapshot.getSensorsState().put(event.getId(), mapToSensorState(event));
        snapshot.setTimestamp(Instant.now(clock));
        repository.save(snapshot);
        sender.send(snapshot);
    }

    private SensorSnapshotAvro newSnapshot(final String hubId) {
        return SensorSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(Instant.now(clock))
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
