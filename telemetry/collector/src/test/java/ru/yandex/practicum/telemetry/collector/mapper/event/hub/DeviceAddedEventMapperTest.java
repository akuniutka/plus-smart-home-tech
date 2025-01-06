package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.DeviceTypeMapper;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestHubEventProtoWithNoPayload;

class DeviceAddedEventMapperTest {

    private static final String SENSOR_ID = "test.light.sensor.1";
    private DeviceTypeMapper mockDeviceTypeMapper;

    private DeviceAddedEventMapper mapper;

    private static List<Arguments> provideTestValues() {
        final List<DeviceTypeProto> deviceTypesProto = TestModels.getPossibleDeviceTypes();
        final DeviceTypeAvro[] deviceTypesAvro = DeviceTypeAvro.values();
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < deviceTypesProto.size(); i++) {
            DeviceTypeProto deviceTypeProto = deviceTypesProto.get(i);
            DeviceTypeAvro deviceTypeAvro = deviceTypesAvro[i % deviceTypesAvro.length];
            testValues.add(Arguments.of(deviceTypeProto, getTestHubEventProto(deviceTypeProto), deviceTypeAvro,
                            getTestHubEventAvro(deviceTypeAvro)));
        }
        return testValues;
    }

    private static HubEventProto getTestHubEventProto(final DeviceTypeProto deviceType) {
        return HubEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setDeviceAdded(DeviceAddedEventProto.newBuilder()
                        .setId(SENSOR_ID)
                        .setType(deviceType)
                        .build())
                .build();
    }

    private static HubEventAvro getTestHubEventAvro(final DeviceTypeAvro deviceType) {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(SENSOR_ID)
                        .setType(deviceType)
                        .build())
                .build();
    }

    @BeforeEach
    void setUp() {
        mockDeviceTypeMapper = Mockito.mock(DeviceTypeMapper.class);
        mapper = new DeviceAddedEventMapper(mockDeviceTypeMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockDeviceTypeMapper);
    }

    @Test
    void whenGetPayloadType_ThenReturnDeviceAdded() {
        final HubEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(HubEventProto.PayloadCase.DEVICE_ADDED));
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenMapToAvro_ThenReturnCorrectHubEventAvro(
            final DeviceTypeProto deviceTypeProto,
            final HubEventProto hubEventProto,
            final DeviceTypeAvro deviceTypeAvro,
            final HubEventAvro hubEventAvro
    ) {
        Mockito.when(mockDeviceTypeMapper.mapToAvro(any())).thenReturn(deviceTypeAvro);

        final HubEventAvro eventAvro = mapper.mapToAvro(hubEventProto);

        Mockito.verify(mockDeviceTypeMapper).mapToAvro(deviceTypeProto);
        assertThat(eventAvro, equalTo(hubEventAvro));
    }

    @Test
    void whenMapToAvroAndWrongPayloadType_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(getTestHubEventProtoWithNoPayload()));

        assertThat(e.getMessage(), equalTo("Unknown payload type: PAYLOAD_NOT_SET"));
    }
}