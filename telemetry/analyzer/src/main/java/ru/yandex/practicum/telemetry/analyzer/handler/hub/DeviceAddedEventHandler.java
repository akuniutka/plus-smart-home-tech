package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Device;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceService;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    private final DeviceService service;

    public DeviceAddedEventHandler(final DeviceService service) {
        this.service = service;
    }

    @Override
    public Class<DeviceAddedEventAvro> getPayloadType() {
        return DeviceAddedEventAvro.class;
    }

    @Override
    protected DeviceAddedEventAvro cast(final Object payload) {
        return (DeviceAddedEventAvro) payload;
    }

    @Override
    protected void handleInternally(final String hubId, final DeviceAddedEventAvro payload) {
        final Device device = new Device();
        device.setId(payload.getId());
        device.setHubId(hubId);
        device.setType(payload.getType());
        service.register(device);
    }
}
