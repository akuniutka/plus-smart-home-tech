package ru.yandex.practicum.telemetry.router.device.sensor;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.router.configuration.ValueRange;

public class MotionSensor extends AbstractSensor {

    private final ValueRange linkQualityRange;
    private final ValueRange voltageRange;
    private int linkQuality;
    private int voltage;
    private boolean isMotion;

    public MotionSensor(final String id, final ValueRange linkQuality, ValueRange voltage) {
        super(id);
        this.linkQualityRange = linkQuality;
        this.voltageRange = voltage;
        this.linkQuality = getMidValue(linkQualityRange);
        this.voltage = getMidValue(voltageRange);
        this.isMotion = false;
    }

    @Override
    public DeviceTypeProto getType() {
        return DeviceTypeProto.MOTION_SENSOR;
    }

    @Override
    public void addPayload(final SensorEventProto.Builder builder) {
        linkQuality = getRandomValue(linkQuality, 1, linkQualityRange);
        voltage = getRandomValue(voltage, 1, voltageRange);
        isMotion = random.nextBoolean();
        builder.setMotionSensorEvent(MotionSensorProto.newBuilder()
                .setLinkQuality(linkQuality)
                .setVoltage(voltage)
                .setMotion(isMotion)
                .build());
    }
}
