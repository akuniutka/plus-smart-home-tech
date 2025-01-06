package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SensorEventMapperFactoryImplTest {

    private static List<SensorEventProto.PayloadCase> payloadTypes;
    private static List<SensorEventMapper> mockMappers;

    private SensorEventMapperFactory factory;

    @BeforeAll
    static void init() {
        payloadTypes = TestModels.getPossibleSensorEventPayloadTypes();
        mockMappers = new ArrayList<>();
        for (SensorEventProto.PayloadCase payloadType : payloadTypes) {
            SensorEventMapper mockMapper = Mockito.mock(SensorEventMapper.class);
            Mockito.when(mockMapper.getPayloadType()).thenReturn(payloadType);
            mockMappers.add(mockMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < payloadTypes.size(); i++) {
            testValues.add(Arguments.of(payloadTypes.get(i), mockMappers.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockMappers.forEach(Mockito::clearInvocations);
        factory = new SensorEventMapperFactoryImpl(new HashSet<>(mockMappers));
    }

    @AfterEach
    void tearDown() {
        for (SensorEventMapper mockMapper : mockMappers) {
            Mockito.verify(mockMapper).getPayloadType();
            Mockito.verifyNoMoreInteractions(mockMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenGetMapperAndPayloadTypeSet_ThenReturnCorrectMapper(final SensorEventProto.PayloadCase payloadType,
            final SensorEventMapper expectedMapper) {
        final SensorEventMapper actualMapper = factory.getMapper(payloadType);

        assertThat(actualMapper, sameInstance(expectedMapper));
    }

    @Test
    void whenGetMapperAndPayloadTypeNotSet_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper(SensorEventProto.PayloadCase.PAYLOAD_NOT_SET));

        assertThat(e.getMessage(), equalTo("No sensor event mapper found for payload type PAYLOAD_NOT_SET"));
    }
}