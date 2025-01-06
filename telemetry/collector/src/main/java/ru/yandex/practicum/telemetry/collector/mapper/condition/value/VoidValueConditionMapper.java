package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

@Component
public class VoidValueConditionMapper extends BaseConditionMapper<Void> {

    public VoidValueConditionMapper(
            final ConditionTypeMapper conditionTypeMapper,
            final OperationMapper operationMapper
    ) {
        super(conditionTypeMapper, operationMapper);
    }

    @Override
    public ScenarioConditionProto.ValueCase getValueType() {
        return ScenarioConditionProto.ValueCase.VALUE_NOT_SET;
    }

    @Override
    protected Void mapValue(final ScenarioConditionProto condition) {
        return null;
    }
}
