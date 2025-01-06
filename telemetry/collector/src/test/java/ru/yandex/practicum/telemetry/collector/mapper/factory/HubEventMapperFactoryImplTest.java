package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HubEventMapperFactoryImplTest {

    private static List<HubEventProto.PayloadCase> payloadTypes;
    private static List<HubEventMapper> mockMappers;

    private HubEventMapperFactory factory;

    @BeforeAll
    static void init() {
        payloadTypes = TestModels.getPossibleHubEventPayloadTypes();
        mockMappers = new ArrayList<>();
        for (HubEventProto.PayloadCase payloadType : payloadTypes) {
            HubEventMapper mockMapper = Mockito.mock(HubEventMapper.class);
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
        factory = new HubEventMapperFactoryImpl(new HashSet<>(mockMappers));
    }

    @AfterEach
    void tearDown() {
        for (HubEventMapper mockMapper : mockMappers) {
            Mockito.verify(mockMapper).getPayloadType();
            Mockito.verifyNoMoreInteractions(mockMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenGetMapperAndPayloadTypeSet_ThenReturnCorrectMapper(final HubEventProto.PayloadCase payloadType,
            final HubEventMapper expectedMapper) {
        final HubEventMapper actualMapper = factory.getMapper(payloadType);

        assertThat(actualMapper, sameInstance(expectedMapper));
    }

    @Test
    void whenGetMapperAndPayloadTypeNotSet_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper(HubEventProto.PayloadCase.PAYLOAD_NOT_SET));

        assertThat(e.getMessage(), equalTo("No hub event mapper found for payload type PAYLOAD_NOT_SET"));
    }
}