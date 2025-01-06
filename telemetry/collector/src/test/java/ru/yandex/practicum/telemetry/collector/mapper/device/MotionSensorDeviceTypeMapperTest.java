package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MotionSensorDeviceTypeMapperTest {

    private MotionSensorDeviceTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MotionSensorDeviceTypeMapper();
    }

    @Test
    void whenGetDeviceTypeProto_ThenReturnMotionSensor() {
        final DeviceTypeProto deviceType = mapper.getDeviceTypeProto();

        assertThat(deviceType, equalTo(DeviceTypeProto.MOTION_SENSOR));
    }

    @Test
    void whenGetDeviceTypeAvro_ThenReturnMotionSensor() {
        final DeviceTypeAvro deviceType = mapper.getDeviceTypeAvro();

        assertThat(deviceType, equalTo(DeviceTypeAvro.MOTION_SENSOR));
    }
}