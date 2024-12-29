package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.extractor.ConditionValueExtractor;
import ru.yandex.practicum.telemetry.analyzer.service.ConditionValueExtractorFactory;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class ConditionValueExtractorFactoryImpl implements ConditionValueExtractorFactory {

    private final Map<String, ConditionValueExtractor> extractors;

    public ConditionValueExtractorFactoryImpl(final Set<ConditionValueExtractor> extractors) {
        this.extractors = extractors.stream()
                .collect(Collectors.toMap(ConditionValueExtractor::getValueType, Function.identity()));
    }

    @Override
    public Supplier<Object> getExtractor(final ScenarioCondition condition) {
        return () -> {
            final String valueType = condition.getValueType();
            if (valueType == null) {
                return null;
            }
            if (!extractors.containsKey(valueType)) {
                throw new IllegalArgumentException("No extractor for condition value of type " + valueType);
            }
            return extractors.get(valueType).extractValue(condition.getValue());
        };
    }
}
