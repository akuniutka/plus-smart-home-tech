package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

@Component
public class IntegerValueConditionMapper extends BaseConditionMapper<Integer> {

    public IntegerValueConditionMapper(
            final ConditionTypeMapper conditionTypeMapper,
            final OperationMapper operationMapper
    ) {
        super(conditionTypeMapper, operationMapper);
    }

    @Override
    public ScenarioConditionProto.ValueCase getValueType() {
        return ScenarioConditionProto.ValueCase.INT_VALUE;
    }

    @Override
    protected Integer mapValue(final ScenarioConditionProto condition) {
        return condition.getIntValue();
    }
}
