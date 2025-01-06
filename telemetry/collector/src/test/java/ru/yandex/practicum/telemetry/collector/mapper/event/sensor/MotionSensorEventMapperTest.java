package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToObject;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestSensorEventProtoWithNoPayload;

class MotionSensorEventMapperTest {

    private static final String SENSOR_ID = "test.motion.sensor.1";
    private static final int LINK_QUALITY = 95;
    private static final boolean MOTION = true;
    private static final int VOLTAGE = 220;

    private MotionSensorEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MotionSensorEventMapper();
    }

    @Test
    void whenGetPayloadType_ThenReturnMotionSensorEvent() {
        final SensorEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT));
    }

    @Test
    void whenMapToAvro_ThenReturnCorrectSensorEventAvro() {
        final SensorEventAvro eventAvro = mapper.mapToAvro(getTestSensorEventProto());

        assertThat(eventAvro, equalToObject(getTestSensorEventAvro()));
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
                .setMotionSensorEvent(MotionSensorProto.newBuilder()
                        .setLinkQuality(LINK_QUALITY)
                        .setMotion(MOTION)
                        .setVoltage(VOLTAGE)
                        .build())
                .build();
    }

    private SensorEventAvro getTestSensorEventAvro() {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(LINK_QUALITY)
                        .setMotion(MOTION)
                        .setVoltage(VOLTAGE)
                        .build())
                .build();
    }
}