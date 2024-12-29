package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.telemetry.analyzer.extractor.SensorValueExtractor;
import ru.yandex.practicum.telemetry.analyzer.service.SensorValueExtractorFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class SensorValueExtractorFactoryImpl implements SensorValueExtractorFactory {

    private final Map<Class<?>, Map<ConditionTypeAvro, SensorValueExtractor>> extractors;

    public SensorValueExtractorFactoryImpl(final Set<SensorValueExtractor> extractors) {
        this.extractors = extractors.stream()
                .collect(Collectors.groupingBy(SensorValueExtractor::getDataType,
                        Collectors.toMap(SensorValueExtractor::getMetricType, Function.identity())));
    }

    @Override
    public Supplier<Object> getExtractor(final Object sensorData, final ConditionTypeAvro metric) {
        return () -> {
            final SensorValueExtractor extractor =
                    extractors.getOrDefault(sensorData.getClass(), Collections.emptyMap()).get(metric);
            if (extractor == null) {
                return null;
            }
            return extractor.extractValue(sensorData);
        };
    }
}
