package ru.yandex.practicum.telemetry.router.device.sensor;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.router.configuration.ValueRange;

public class LightSensor extends AbstractSensor {

    private final ValueRange luminosityRange;
    private final ValueRange linkQualityRange;
    private int luminosity;
    private int linkQuality;

    public LightSensor(final String id, final ValueRange luminosity) {
        super(id);
        this.luminosityRange = luminosity;
        this.linkQualityRange = new ValueRange(100, 100);
        this.luminosity = getMidValue(luminosityRange);
        this.linkQuality = getMidValue(linkQualityRange);
    }

    @Override
    public DeviceTypeProto getType() {
        return DeviceTypeProto.LIGHT_SENSOR;
    }

    @Override
    public void addPayload(final SensorEventProto.Builder builder) {
        luminosity = getRandomValue(luminosity, 5, luminosityRange);
        linkQuality = getRandomValue(linkQuality, 1, linkQualityRange);
        builder.setLightSensorEvent(LightSensorProto.newBuilder()
                .setLuminosity(luminosity)
                .setLinkQuality(linkQuality)
                .build());
    }
}
