package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedEventMapper extends BaseHubEventMapper<DeviceRemovedEventAvro> {

    @Override
    public HubEventProto.PayloadCase getPayloadType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    protected DeviceRemovedEventAvro mapPayload(final HubEventProto event) {
        final DeviceRemovedEventProto payload = event.getDeviceRemoved();
        return DeviceRemovedEventAvro.newBuilder()
                .setId(payload.getId())
                .build();
    }
}
