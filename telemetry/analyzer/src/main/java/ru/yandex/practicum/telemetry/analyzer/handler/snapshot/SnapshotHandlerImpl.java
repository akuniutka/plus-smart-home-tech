package ru.yandex.practicum.telemetry.analyzer.handler.snapshot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.SnapshotHandler;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceActionExecutor;
import ru.yandex.practicum.telemetry.analyzer.service.PredicateBuilder;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

import java.util.List;
import java.util.function.Predicate;

@Component
public class SnapshotHandlerImpl implements SnapshotHandler {

    private static final Logger log = LoggerFactory.getLogger(SnapshotHandlerImpl.class);
    private final ScenarioService scenarioService;
    private final PredicateBuilder predicateBuilder;
    private final DeviceActionExecutor executor;

    public SnapshotHandlerImpl(
            final ScenarioService scenarioService,
            final PredicateBuilder predicateBuilder,
            final DeviceActionExecutor executor) {
        this.scenarioService = scenarioService;
        this.predicateBuilder = predicateBuilder;
        this.executor = executor;
    }

    @Override
    public void handle(final SensorSnapshotAvro snapshot) {
        log.info("Received snapshot: hubId = {}, timestamp = {}", snapshot.getHubId(), snapshot.getTimestamp());
        log.debug("Snapshot = {}", snapshot);
        scenarioService.getByHubId(snapshot.getHubId()).stream()
                .filter(scenario -> toPredicate(scenario.getConditions()).test(snapshot))
                .forEach(executor::execute);
        log.info("Processed snapshot: hubId = {}, timestamp = {}", snapshot.getHubId(), snapshot.getTimestamp());
    }

    private Predicate<SensorSnapshotAvro> toPredicate(final List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(predicateBuilder::toPredicate)
                .reduce(Predicate::and)
                .orElseThrow();
    }
}
