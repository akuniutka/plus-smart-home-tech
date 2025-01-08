package ru.yandex.practicum.telemetry.aggregator.util;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class TestModels {

    public static final String HUB_ID = "test.hub.1";
    public static final String SENSOR_ID = "test.temperature.sensor.1";
    public static final String ANOTHER_SENSOR_ID = "test.temperature.sensor.2";
    public static final Instant TIMESTAMP = Instant.parse("2000-01-31T13:30:55.123Z");
    public static final Instant PAST_TIMESTAMP = TIMESTAMP.minusSeconds(60L);
    public static final Instant NEW_TIMESTAMP = TIMESTAMP.plusSeconds(60L);
    public static final int TEMPERATURE_C = 20;
    public static final int NEW_TEMPERATURE_C = 25;

    private TestModels() {
    }

    public static SensorSnapshotAvro getTestSnapshot(final String hubId, final SensorEventAvro... events) {
        return SensorSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(NEW_TIMESTAMP)
                .setSensorsState(convertSensorData(events))
                .build();
    }

    public static SensorEventAvro getTestEvent(final String sensorId) {
        return getTestEvent(sensorId, TIMESTAMP, TEMPERATURE_C);
    }

    public static SensorEventAvro getTestEvent(final Instant timestamp, final int temperatureC) {
        return getTestEvent(SENSOR_ID, timestamp, temperatureC);
    }

    public static SensorEventAvro getTestEvent(final String sensorId, final Instant timestamp, final int temperatureC) {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(sensorId)
                .setTimestamp(timestamp)
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temperatureC)
                        .setTemperatureF(convertCelsiusToFahrenheit(temperatureC))
                        .build())
                .build();
    }

    private static Map<String, SensorStateAvro> convertSensorData(final SensorEventAvro... events) {
        return Arrays.stream(events)
                .collect(Collectors.toMap(SensorEventAvro::getId,
                        TestModels::mapEventPayloadToSnapshotData));
    }

    private static SensorStateAvro mapEventPayloadToSnapshotData(final SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }

    private static int convertCelsiusToFahrenheit(final int temperatureC) {
        return (int) (temperatureC * 1.8 + 32);
    }
}
