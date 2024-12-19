package ru.yandex.practicum.telemetry.router.device.hub;

import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.router.device.sensor.AbstractSensor;

import java.util.List;

public class DeviceAdder extends AbstractHubDevice {

    private final List<? extends AbstractSensor> sensors;

    public DeviceAdder(final List<? extends AbstractSensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public void addPayload(final HubEventProto.Builder builder) {
        final int sensorIndex = random.nextInt(0, sensors.size());
        final AbstractSensor sensor = sensors.get(sensorIndex);
        builder.setDeviceAdded(DeviceAddedEventProto.newBuilder()
                .setId(sensor.getId())
                .setType(sensor.getType())
                .build());
    }
}
