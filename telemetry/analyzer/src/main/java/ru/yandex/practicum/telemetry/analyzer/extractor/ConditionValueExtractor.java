package ru.yandex.practicum.telemetry.analyzer.extractor;

public interface ConditionValueExtractor {

    String getValueType();

    Object extractValue(String data);
}
