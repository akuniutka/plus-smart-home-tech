package ru.yandex.practicum.telemetry.router.device.sensor;

import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.router.configuration.ValueRange;

public class ClimateSensor extends AbstractSensor {

    private final ValueRange temperatureRange;
    private final ValueRange humidityRange;
    private final ValueRange co2LevelRange;
    private int temperature;
    private int humidity;
    private int co2Level;

    public ClimateSensor(final String id, final ValueRange temperature, final ValueRange humidity,
            final ValueRange co2Level) {
        super(id);
        this.temperatureRange = temperature;
        this.humidityRange = humidity;
        this.co2LevelRange = co2Level;
        this.temperature = getMidValue(temperatureRange);
        this.humidity = getMidValue(humidityRange);
        this.co2Level = getMidValue(co2LevelRange);
    }

    @Override
    public DeviceTypeProto getType() {
        return DeviceTypeProto.CLIMATE_SENSOR;
    }

    @Override
    public void addPayload(final SensorEventProto.Builder builder) {
        temperature = getRandomValue(temperature, 1, temperatureRange);
        humidity = getRandomValue(humidity, 1, humidityRange);
        co2Level = getRandomValue(co2Level, 50, co2LevelRange);
        builder.setClimateSensorEvent(ClimateSensorProto.newBuilder()
                .setTemperatureC(temperature)
                .setHumidity(humidity)
                .setCo2Level(co2Level)
                .build());
    }
}
