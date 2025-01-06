package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public interface OperationMapper {

    ConditionOperationAvro mapToAvro(ConditionOperationProto operation);
}
