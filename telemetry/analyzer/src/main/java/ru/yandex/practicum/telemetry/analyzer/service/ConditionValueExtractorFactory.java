package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;

import java.util.function.Supplier;

public interface ConditionValueExtractorFactory {

    Supplier<Object> getExtractor(ScenarioCondition condition);
}
