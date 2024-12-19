package ru.yandex.practicum.telemetry.router.device.sensor;

import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.telemetry.router.configuration.ValueRange;

public class TemperatureSensor extends AbstractSensor {

    private final ValueRange temperatureCelsiusRange;
    private int temperatureCelsius;

    public TemperatureSensor(final String id, final ValueRange temperature) {
        super(id);
        this.temperatureCelsiusRange = temperature;
        this.temperatureCelsius = getMidValue(temperatureCelsiusRange);
    }

    @Override
    public DeviceTypeProto getType() {
        return DeviceTypeProto.TEMPERATURE_SENSOR;
    }

    @Override
    public void addPayload(final SensorEventProto.Builder builder) {
        temperatureCelsius = getRandomValue(temperatureCelsius, 1, temperatureCelsiusRange);
        final int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
        builder.setTemperatureSensorEvent(TemperatureSensorProto.newBuilder()
                .setTemperatureC(temperatureCelsius)
                .setTemperatureF(temperatureFahrenheit)
                .build());
    }
}
