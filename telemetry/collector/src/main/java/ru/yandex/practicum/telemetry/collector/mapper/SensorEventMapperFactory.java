package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventMapperFactory {

    SensorEventMapper getMapper(SensorEventProto.PayloadCase payloadType);
}
