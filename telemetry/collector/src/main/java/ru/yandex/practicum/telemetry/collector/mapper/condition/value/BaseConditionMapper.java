package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

public abstract class BaseConditionMapper<T> implements ConditionMapper {

    protected final ConditionTypeMapper conditionTypeMapper;
    protected final OperationMapper operationMapper;

    protected BaseConditionMapper(final ConditionTypeMapper conditionTypeMapper, final OperationMapper operationMapper) {
        this.conditionTypeMapper = conditionTypeMapper;
        this.operationMapper = operationMapper;
    }

    @Override
    public ScenarioConditionAvro mapToAvro(final ScenarioConditionProto condition) {
        if (!condition.getValueCase().equals(getValueType())) {
            throw new IllegalArgumentException("Unknown value type: " + condition.getValueCase());
        }

        final T value = mapValue(condition);

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(conditionTypeMapper.mapToAvro(condition.getType()))
                .setOperation(operationMapper.mapToAvro(condition.getOperation()))
                .setValue(value)
                .build();
    }

    protected abstract T mapValue(ScenarioConditionProto condition);
}
