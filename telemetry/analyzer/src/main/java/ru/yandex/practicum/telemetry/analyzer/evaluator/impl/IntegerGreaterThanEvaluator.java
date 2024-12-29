package ru.yandex.practicum.telemetry.analyzer.evaluator.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

@Component
public class IntegerGreaterThanEvaluator extends BaseIntegerEvaluator {

    @Override
    public ConditionOperationAvro getOperationType() {
        return ConditionOperationAvro.GREATER_THAN;
    }

    @Override
    protected boolean evaluateInternally(final Integer operandA, final Integer operandB) {
        return operandA > operandB;
    }
}
