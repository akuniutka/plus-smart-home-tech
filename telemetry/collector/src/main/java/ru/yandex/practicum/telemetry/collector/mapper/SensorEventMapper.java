package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventMapper {

    SensorEventProto.PayloadCase getPayloadType();

    SensorEventAvro mapToAvro(SensorEventProto eventProto);
}
