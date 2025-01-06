package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LightSensorDeviceTypeMapperTest {

    private LightSensorDeviceTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LightSensorDeviceTypeMapper();
    }

    @Test
    void whenGetDeviceTypeProto_ThenReturnLightSensor() {
        final DeviceTypeProto deviceType = mapper.getDeviceTypeProto();

        assertThat(deviceType, equalTo(DeviceTypeProto.LIGHT_SENSOR));
    }

    @Test
    void whenGetDeviceTypeAvro_ThenReturnLightSensor() {
        final DeviceTypeAvro deviceType = mapper.getDeviceTypeAvro();

        assertThat(deviceType, equalTo(DeviceTypeAvro.LIGHT_SENSOR));
    }
}