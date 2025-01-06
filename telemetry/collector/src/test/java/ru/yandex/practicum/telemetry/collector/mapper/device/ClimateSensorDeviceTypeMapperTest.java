package ru.yandex.practicum.telemetry.collector.mapper.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ClimateSensorDeviceTypeMapperTest {

    private ClimateSensorDeviceTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClimateSensorDeviceTypeMapper();
    }

    @Test
    void whenGetDeviceTypeProto_ThenReturnClimateSensor() {
        final DeviceTypeProto deviceType = mapper.getDeviceTypeProto();

        assertThat(deviceType, equalTo(DeviceTypeProto.CLIMATE_SENSOR));
    }

    @Test
    void whenGetDeviceTypeAvro_ThenReturnClimateSensor() {
        final DeviceTypeAvro deviceType = mapper.getDeviceTypeAvro();

        assertThat(deviceType, equalTo(DeviceTypeAvro.CLIMATE_SENSOR));
    }
}