package ru.yandex.practicum.telemetry.analyzer.extractor.condition;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.extractor.ConditionValueExtractor;

@Component
public class ConditionIntegerValueExtractor implements ConditionValueExtractor {

    @Override
    public String getValueType() {
        return Integer.class.getSimpleName();
    }

    @Override
    public Object extractValue(final String data) {
        return Integer.parseInt(data);
    }
}
