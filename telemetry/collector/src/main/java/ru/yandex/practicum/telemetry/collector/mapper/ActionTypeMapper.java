package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

public interface ActionTypeMapper {

    ActionTypeAvro mapToAvro(ActionTypeProto actionType);
}
