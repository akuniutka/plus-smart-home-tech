package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

public interface ConditionMapperFactory {

    ConditionMapper getMapper(ScenarioConditionProto.ValueCase valueType);
}
