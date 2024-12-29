package ru.yandex.practicum.telemetry.analyzer.evaluator.impl;

public abstract class BaseBooleanEvaluator extends BaseOperationEvaluator<Boolean> {

    @Override
    public Class<?> getOperandType() {
        return Boolean.class;
    }

    @Override
    protected Boolean cast(final Object operand) {
        return (Boolean) operand;
    }
}
