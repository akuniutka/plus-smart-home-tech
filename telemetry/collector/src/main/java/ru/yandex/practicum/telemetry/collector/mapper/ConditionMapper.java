package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

public interface ConditionMapper {

    ScenarioConditionProto.ValueCase getValueType();

    ScenarioConditionAvro mapToAvro(ScenarioConditionProto condition);
}
