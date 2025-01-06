package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class SwitchSensorDeviceTypeMapperTest {

    private SwitchSensorDeviceTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SwitchSensorDeviceTypeMapper();
    }

    @Test
    void whenGetDeviceTypeProto_ThenReturnSwitchSensor() {
        final DeviceTypeProto deviceType = mapper.getDeviceTypeProto();

        assertThat(deviceType, equalTo(DeviceTypeProto.SWITCH_SENSOR));
    }

    @Test
    void whenGetDeviceTypeAvro_ThenReturnSwitchSensor() {
        final DeviceTypeAvro deviceType = mapper.getDeviceTypeAvro();

        assertThat(deviceType, equalTo(DeviceTypeAvro.SWITCH_SENSOR));
    }
}