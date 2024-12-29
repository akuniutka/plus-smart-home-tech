package ru.yandex.practicum.telemetry.analyzer.extractor.condition;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.extractor.ConditionValueExtractor;

@Component
public class ConditionBooleanValueExtractor implements ConditionValueExtractor {

    @Override
    public String getValueType() {
        return Boolean.class.getSimpleName();
    }

    @Override
    public Object extractValue(final String data) {
        return Boolean.parseBoolean(data);
    }
}
