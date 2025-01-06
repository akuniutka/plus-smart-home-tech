package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.DeviceTypeMapper;

@Component
public class DeviceAddedEventMapper extends BaseHubEventMapper<DeviceAddedEventAvro> {

    private final DeviceTypeMapper deviceTypeMapper;

    public DeviceAddedEventMapper(final DeviceTypeMapper deviceTypeMapper) {
        this.deviceTypeMapper = deviceTypeMapper;
    }

    @Override
    public HubEventProto.PayloadCase getPayloadType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapPayload(final HubEventProto event) {
        final DeviceAddedEventProto payload = event.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(payload.getId())
                .setType(deviceTypeMapper.mapToAvro(payload.getType()))
                .build();
    }
}
