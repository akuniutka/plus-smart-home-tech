package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventMapperFactory {

    HubEventMapper getMapper(HubEventProto.PayloadCase payloadType);
}
