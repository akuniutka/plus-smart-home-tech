package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

public interface ConditionTypeMapper {

    ConditionTypeAvro mapToAvro(ConditionTypeProto conditionType);
}
