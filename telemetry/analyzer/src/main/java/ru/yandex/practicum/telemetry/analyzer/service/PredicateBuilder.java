package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;

import java.util.function.Predicate;

public interface PredicateBuilder {

    Predicate<SensorSnapshotAvro> toPredicate(ScenarioCondition condition);
}
