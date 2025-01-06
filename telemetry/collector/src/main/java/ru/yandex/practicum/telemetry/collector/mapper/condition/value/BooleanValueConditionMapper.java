package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

@Component
public class BooleanValueConditionMapper extends BaseConditionMapper<Boolean> {

    public BooleanValueConditionMapper(
            final ConditionTypeMapper conditionTypeMapper,
            final OperationMapper operationMapper
    ) {
        super(conditionTypeMapper, operationMapper);
    }

    @Override
    public ScenarioConditionProto.ValueCase getValueType() {
        return ScenarioConditionProto.ValueCase.BOOL_VALUE;
    }

    @Override
    protected Boolean mapValue(final ScenarioConditionProto condition) {
        return condition.getBoolValue();
    }
}
