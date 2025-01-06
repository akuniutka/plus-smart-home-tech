package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.DeviceTypeMapper;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeviceTypeMapperImplTest {

    private static List<DeviceTypeProto> deviceTypesProto;
    private static List<DeviceTypeAvro> deviceTypesAvro;
    private static List<DeviceTypeMapperImpl.SpecificDeviceTypeMapper> mockSpecificMappers;

    private DeviceTypeMapper mapper;

    @BeforeAll
    static void init() {
        deviceTypesProto = TestModels.getPossibleDeviceTypes();
        deviceTypesAvro = new ArrayList<>();
        final DeviceTypeAvro[] deviceTypeAvroValues = DeviceTypeAvro.values();
        mockSpecificMappers = new ArrayList<>();
        for (int i = 0; i < deviceTypesProto.size(); i++) {
            deviceTypesAvro.add(deviceTypeAvroValues[i % deviceTypeAvroValues.length]);
            DeviceTypeMapperImpl.SpecificDeviceTypeMapper mockSpecificMapper =
                    Mockito.mock(DeviceTypeMapperImpl.SpecificDeviceTypeMapper.class);
            Mockito.when(mockSpecificMapper.getDeviceTypeProto()).thenReturn(deviceTypesProto.get(i));
            Mockito.when(mockSpecificMapper.getDeviceTypeAvro()).thenReturn(deviceTypesAvro.get(i));
            mockSpecificMappers.add(mockSpecificMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < deviceTypesProto.size(); i++) {
            testValues.add(Arguments.of(deviceTypesProto.get(i), mockSpecificMappers.get(i), deviceTypesAvro.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockSpecificMappers.forEach(Mockito::clearInvocations);
        mapper = new DeviceTypeMapperImpl(new HashSet<>(mockSpecificMappers));
    }

    @AfterEach
    void tearDown() {
        for (DeviceTypeMapperImpl.SpecificDeviceTypeMapper mockSpecificMapper : mockSpecificMappers) {
            Mockito.verify(mockSpecificMapper).getDeviceTypeProto();
            Mockito.verifyNoMoreInteractions(mockSpecificMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenMapToAvroAndSpecificMapperExist_ThenReturnCorrectDeviceTypeAvro(
            final DeviceTypeProto deviceTypeProto,
            final DeviceTypeMapperImpl.SpecificDeviceTypeMapper mockSpecificMapper,
            final DeviceTypeAvro expectedDeviceTypeAvro
    ) {
        final DeviceTypeAvro actualDeviceTypeAvro = mapper.mapToAvro(deviceTypeProto);

        Mockito.verify(mockSpecificMapper).getDeviceTypeAvro();
        assertThat(actualDeviceTypeAvro, equalTo(expectedDeviceTypeAvro));
    }

    @Test
    void whenMapToAvroAndSpecificMapperNotExist_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(DeviceTypeProto.UNRECOGNIZED));

        assertThat(e.getMessage(), equalTo("No mapper found for device type UNRECOGNIZED"));
    }
}