package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionTypeMapperImplTest {

    private static List<ConditionTypeProto> conditionTypesProto;
    private static List<ConditionTypeAvro> conditionTypesAvro;
    private static List<ConditionTypeMapperImpl.SpecificConditionTypeMapper> mockSpecificMappers;

    private ConditionTypeMapper mapper;

    @BeforeAll
    static void init() {
        conditionTypesProto = TestModels.getPossibleConditionTypes();
        conditionTypesAvro = TestModels.getTestConditionTypesAvro();
        mockSpecificMappers = new ArrayList<>();
        for (int i = 0; i < conditionTypesProto.size(); i++) {
            ConditionTypeMapperImpl.SpecificConditionTypeMapper mockSpecificMapper =
                    Mockito.mock(ConditionTypeMapperImpl.SpecificConditionTypeMapper.class);
            Mockito.when(mockSpecificMapper.getConditionTypeProto()).thenReturn(conditionTypesProto.get(i));
            Mockito.when(mockSpecificMapper.getConditionTypeAvro()).thenReturn(conditionTypesAvro.get(i));
            mockSpecificMappers.add(mockSpecificMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < conditionTypesProto.size(); i++) {
            testValues.add(Arguments.of(conditionTypesProto.get(i), mockSpecificMappers.get(i),
                    conditionTypesAvro.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockSpecificMappers.forEach(Mockito::clearInvocations);
        mapper = new ConditionTypeMapperImpl(new HashSet<>(mockSpecificMappers));
    }

    @AfterEach
    void tearDown() {
        for (ConditionTypeMapperImpl.SpecificConditionTypeMapper mockSpecificMapper : mockSpecificMappers) {
            Mockito.verify(mockSpecificMapper).getConditionTypeProto();
            Mockito.verifyNoMoreInteractions(mockSpecificMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenMapToAvroAndSpecificMapperExist_ThenReturnCorrectConditionTypeAvro(
            final ConditionTypeProto conditionTypeProto,
            final ConditionTypeMapperImpl.SpecificConditionTypeMapper mockSpecificMapper,
            final ConditionTypeAvro expectedConditionTypeAvro
    ) {
        final ConditionTypeAvro actualConditionTypeAvro = mapper.mapToAvro(conditionTypeProto);

        Mockito.verify(mockSpecificMapper).getConditionTypeAvro();
        assertThat(actualConditionTypeAvro, equalTo(expectedConditionTypeAvro));
    }

    @Test
    void whenMapToAvroAndSpecificMapperNotExist_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(ConditionTypeProto.UNRECOGNIZED));

        assertThat(e.getMessage(), equalTo("No mapper found for condition type UNRECOGNIZED"));
    }
}