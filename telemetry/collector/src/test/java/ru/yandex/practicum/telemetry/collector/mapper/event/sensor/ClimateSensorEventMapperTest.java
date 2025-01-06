package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestSensorEventProtoWithNoPayload;

class ClimateSensorEventMapperTest {

    private static final String SENSOR_ID = "test.climate.sensor.1";
    private static final int TEMPERATURE_C = 20;
    private static final int HUMIDITY = 60;
    private static final int CO2_LEVEL = 400;

    private ClimateSensorEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClimateSensorEventMapper();
    }

    @Test
    void whenGetPayloadType_ThenReturnClimateSensorEvent() {
        final SensorEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT));
    }

    @Test
    void whenMapToAvro_ThenReturnCorrectSensorEventAvro() {
        final SensorEventAvro eventAvro = mapper.mapToAvro(getTestSensorEventProto());

        assertThat(eventAvro, equalTo(getTestSensorEventAvro()));
    }

    @Test
    void whenMapToAvroAndWrongPayloadType_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(getTestSensorEventProtoWithNoPayload()));

        assertThat(e.getMessage(), equalTo("Unknown payload type: PAYLOAD_NOT_SET"));
    }

    private SensorEventProto getTestSensorEventProto() {
        return SensorEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setClimateSensorEvent(ClimateSensorProto.newBuilder()
                        .setTemperatureC(TEMPERATURE_C)
                        .setHumidity(HUMIDITY)
                        .setCo2Level(CO2_LEVEL)
                        .build())
                .build();
    }

    private SensorEventAvro getTestSensorEventAvro() {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setTemperatureC(TEMPERATURE_C)
                        .setHumidity(HUMIDITY)
                        .setCo2Level(CO2_LEVEL)
                        .build())
                .build();
    }
}