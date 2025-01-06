package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.DeviceTypeMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DeviceTypeMapperImpl implements DeviceTypeMapper {

    private final Map<DeviceTypeProto, SpecificDeviceTypeMapper> mappers;

    public DeviceTypeMapperImpl(final Set<SpecificDeviceTypeMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(SpecificDeviceTypeMapper::getDeviceTypeProto, Function.identity()));
    }

    @Override
    public DeviceTypeAvro mapToAvro(final DeviceTypeProto deviceType) {
        if (!mappers.containsKey(deviceType)) {
            throw new IllegalArgumentException("No mapper found for device type " + deviceType);
        }
        return mappers.get(deviceType).getDeviceTypeAvro();
    }

    public interface SpecificDeviceTypeMapper {

        DeviceTypeProto getDeviceTypeProto();

        DeviceTypeAvro getDeviceTypeAvro();
    }
}
