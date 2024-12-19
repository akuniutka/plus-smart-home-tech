package ru.yandex.practicum.telemetry.router.device.hub;

import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.router.device.sensor.AbstractSensor;

import java.util.List;

public class DeviceRemover extends AbstractHubDevice {

    private final List<? extends AbstractSensor> sensors;

    public DeviceRemover(final List<? extends AbstractSensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public void addPayload(final HubEventProto.Builder builder) {
        final int sensorIndex = random.nextInt(0, sensors.size());
        final AbstractSensor sensor = sensors.get(sensorIndex);
        builder.setDeviceRemoved(DeviceRemovedEventProto.newBuilder()
                .setId(sensor.getId())
                .build());
    }
}
