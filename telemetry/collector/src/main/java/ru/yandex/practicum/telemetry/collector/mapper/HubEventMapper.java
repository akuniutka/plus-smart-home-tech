package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventMapper {

    HubEventProto.PayloadCase getPayloadType();

    HubEventAvro mapToAvro(HubEventProto eventProto);
}
