package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TemperatureSensorDeviceTypeMapperTest {

    private TemperatureSensorDeviceTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TemperatureSensorDeviceTypeMapper();
    }

    @Test
    void whenGetDeviceTypeProto_ThenReturnTemperatureSensor() {
        final DeviceTypeProto deviceType = mapper.getDeviceTypeProto();

        assertThat(deviceType, equalTo(DeviceTypeProto.TEMPERATURE_SENSOR));
    }

    @Test
    void whenGetDeviceTypeAvro_ThenReturnTemperatureSensor() {
        final DeviceTypeAvro deviceType = mapper.getDeviceTypeAvro();

        assertThat(deviceType, equalTo(DeviceTypeAvro.TEMPERATURE_SENSOR));
    }
}