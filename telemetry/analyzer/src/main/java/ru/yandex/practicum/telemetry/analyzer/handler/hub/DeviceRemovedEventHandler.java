package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Device;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceService;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {

    private final DeviceService service;

    public DeviceRemovedEventHandler(final DeviceService service) {
        this.service = service;
    }

    @Override
    public String getPayloadType() {
        return DeviceRemovedEventAvro.class.getName();
    }

    @Override
    protected DeviceRemovedEventAvro cast(final Object payload) {
        return (DeviceRemovedEventAvro) payload;
    }

    @Override
    protected void handleInternally(final String hubId, final DeviceRemovedEventAvro payload) {
        final Device device = new Device();
        device.setId(payload.getId());
        device.setHubId(hubId);
        service.deregister(device);
    }
}
