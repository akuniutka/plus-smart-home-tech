package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public interface DeviceTypeMapper {

    DeviceTypeAvro mapToAvro(DeviceTypeProto deviceType);
}
