package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.factory.DeviceTypeMapperImpl;

@Component
public class LightSensorDeviceTypeMapper implements DeviceTypeMapperImpl.SpecificDeviceTypeMapper {

    @Override
    public DeviceTypeProto getDeviceTypeProto() {
        return DeviceTypeProto.LIGHT_SENSOR;
    }

    @Override
    public DeviceTypeAvro getDeviceTypeAvro() {
        return DeviceTypeAvro.LIGHT_SENSOR;
    }
}