package ru.yandex.practicum.telemetry.analyzer.evaluator;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public interface OperationEvaluator {

    ConditionOperationAvro getOperationType();

    Class<?> getOperandType();

    boolean evaluate(Object operandA, Object operandB);
}
