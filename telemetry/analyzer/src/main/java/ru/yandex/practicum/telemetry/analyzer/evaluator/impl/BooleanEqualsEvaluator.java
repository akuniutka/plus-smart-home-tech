package ru.yandex.practicum.telemetry.analyzer.evaluator.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

@Component
public class BooleanEqualsEvaluator extends BaseBooleanEvaluator {

    @Override
    public ConditionOperationAvro getOperationType() {
        return ConditionOperationAvro.EQUALS;
    }

    @Override
    protected boolean evaluateInternally(final Boolean operandA, final Boolean operandB) {
        return operandA.equals(operandB);
    }
}
