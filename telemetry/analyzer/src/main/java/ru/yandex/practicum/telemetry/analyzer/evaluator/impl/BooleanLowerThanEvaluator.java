package ru.yandex.practicum.telemetry.analyzer.evaluator.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

@Component
public class BooleanLowerThanEvaluator extends BaseBooleanEvaluator {

    @Override
    public ConditionOperationAvro getOperationType() {
        return ConditionOperationAvro.LOWER_THAN;
    }

    @Override
    protected boolean evaluateInternally(final Boolean operandA, final Boolean operandB) {
        return Boolean.compare(operandA, operandB) < 0;
    }
}
