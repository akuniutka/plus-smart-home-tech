package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationMapperImplTest {

    private static List<ConditionOperationProto> operationsProto;
    private static List<ConditionOperationAvro> operationsAvro;
    private static List<OperationMapperImpl.SpecificOperationMapper> mockSpecificMappers;

    private OperationMapper mapper;

    @BeforeAll
    static void init() {
        operationsProto = TestModels.getPossibleOperations();
        operationsAvro = TestModels.getTestOperationsAvro();
        mockSpecificMappers = new ArrayList<>();
        for (int i = 0; i < operationsProto.size(); i++) {
            OperationMapperImpl.SpecificOperationMapper mockSpecificMapper =
                    Mockito.mock(OperationMapperImpl.SpecificOperationMapper.class);
            Mockito.when(mockSpecificMapper.getOperationProto()).thenReturn(operationsProto.get(i));
            Mockito.when(mockSpecificMapper.getOperationAvro()).thenReturn(operationsAvro.get(i));
            mockSpecificMappers.add(mockSpecificMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < operationsProto.size(); i++) {
            testValues.add(Arguments.of(operationsProto.get(i), mockSpecificMappers.get(i), operationsAvro.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockSpecificMappers.forEach(Mockito::clearInvocations);
        mapper = new OperationMapperImpl(new HashSet<>(mockSpecificMappers));
    }

    @AfterEach
    void tearDown() {
        for (OperationMapperImpl.SpecificOperationMapper mockSpecificMapper : mockSpecificMappers) {
            Mockito.verify(mockSpecificMapper).getOperationProto();
            Mockito.verifyNoMoreInteractions(mockSpecificMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenMapToAvroAndSpecificMapperExist_ThenReturnCorrectConditionOperationAvro(
            final ConditionOperationProto operationProto,
            final OperationMapperImpl.SpecificOperationMapper mockSpecificMapper,
            final ConditionOperationAvro expectedOperationAvro
    ) {
        final ConditionOperationAvro actualOperationAvro = mapper.mapToAvro(operationProto);

        Mockito.verify(mockSpecificMapper).getOperationAvro();
        assertThat(actualOperationAvro, equalTo(expectedOperationAvro));
    }

    @Test
    void whenMapToAvroAndSpecificMapperNotExist_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(ConditionOperationProto.UNRECOGNIZED));

        assertThat(e.getMessage(), equalTo("No mapper found for operation UNRECOGNIZED"));
    }
}