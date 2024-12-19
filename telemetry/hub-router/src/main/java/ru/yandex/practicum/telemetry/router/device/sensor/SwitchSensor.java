package ru.yandex.practicum.telemetry.router.device.sensor;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

public class SwitchSensor extends AbstractSensor {

    private boolean isState;

    public SwitchSensor(final String id) {
        super(id);
        this.isState = false;
    }

    @Override
    public DeviceTypeProto getType() {
        return DeviceTypeProto.SWITCH_SENSOR;
    }

    @Override
    public void addPayload(final SensorEventProto.Builder builder) {
        isState = random.nextBoolean();
        builder.setSwitchSensorEvent(SwitchSensorProto.newBuilder()
                .setState(isState)
                .build());
    }
}
