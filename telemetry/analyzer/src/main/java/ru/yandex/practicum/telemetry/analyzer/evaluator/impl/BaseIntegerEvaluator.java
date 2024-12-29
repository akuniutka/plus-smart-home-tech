package ru.yandex.practicum.telemetry.analyzer.evaluator.impl;

public abstract class BaseIntegerEvaluator extends BaseOperationEvaluator<Integer> {

    @Override
    public Class<?> getOperandType() {
        return Integer.class;
    }

    @Override
    protected Integer cast(final Object operand) {
        return (Integer) operand;
    }
}
