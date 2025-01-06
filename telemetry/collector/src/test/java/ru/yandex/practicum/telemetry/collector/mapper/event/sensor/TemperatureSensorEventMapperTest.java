package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestSensorEventProtoWithNoPayload;

class TemperatureSensorEventMapperTest {

    private static final String SENSOR_ID = "test.temperature.sensor.1";
    private static final int TEMPERATURE_C = 20;
    private static final int TEMPERATURE_F = (int) (TEMPERATURE_C * 1.8 + 32);

    private TemperatureSensorEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TemperatureSensorEventMapper();
    }

    @Test
    void whenGetPayloadType_ThenReturnTemperatureSensorEvent() {
        final SensorEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT));
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
                .setTemperatureSensorEvent(TemperatureSensorProto.newBuilder()
                        .setTemperatureC(TEMPERATURE_C)
                        .setTemperatureF(TEMPERATURE_F)
                        .build())
                .build();
    }

    private SensorEventAvro getTestSensorEventAvro() {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(TEMPERATURE_C)
                        .setTemperatureF(TEMPERATURE_F)
                        .build())
                .build();
    }
}