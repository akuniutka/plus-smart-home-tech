package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ActionTypeMapper;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionTypeMapperImplTest {

    private static List<ActionTypeProto> actionTypesProto;
    private static List<ActionTypeAvro> actionTypesAvro;
    private static List<ActionTypeMapperImpl.SpecificActionTypeMapper> mockSpecificMappers;

    private ActionTypeMapper mapper;

    @BeforeAll
    static void init() {
        actionTypesProto = TestModels.getPossibleActionTypes();
        actionTypesAvro = TestModels.getTestActionTypesAvro();
        mockSpecificMappers = new ArrayList<>();
        for (int i = 0; i < actionTypesProto.size(); i++) {
            ActionTypeMapperImpl.SpecificActionTypeMapper mockSpecificMapper =
                    Mockito.mock(ActionTypeMapperImpl.SpecificActionTypeMapper.class);
            Mockito.when(mockSpecificMapper.getActionTypeProto()).thenReturn(actionTypesProto.get(i));
            Mockito.when(mockSpecificMapper.getActionTypeAvro()).thenReturn(actionTypesAvro.get(i));
            mockSpecificMappers.add(mockSpecificMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < actionTypesProto.size(); i++) {
            testValues.add(Arguments.of(actionTypesProto.get(i), mockSpecificMappers.get(i), actionTypesAvro.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockSpecificMappers.forEach(Mockito::clearInvocations);
        mapper = new ActionTypeMapperImpl(new HashSet<>(mockSpecificMappers));
    }

    @AfterEach
    void tearDown() {
        for (ActionTypeMapperImpl.SpecificActionTypeMapper mockSpecificMapper : mockSpecificMappers) {
            Mockito.verify(mockSpecificMapper).getActionTypeProto();
            Mockito.verifyNoMoreInteractions(mockSpecificMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenMapToAvroAndSpecificMapperExist_ThenReturnCorrectActionTypeAvro(
            final ActionTypeProto actionTypeProto,
            final ActionTypeMapperImpl.SpecificActionTypeMapper mockSpecificMapper,
            final ActionTypeAvro expectedActionTypeAvro
    ) {
        final ActionTypeAvro actualActionTypeAvro = mapper.mapToAvro(actionTypeProto);

        Mockito.verify(mockSpecificMapper).getActionTypeAvro();
        assertThat(actualActionTypeAvro, equalTo(expectedActionTypeAvro));
    }

    @Test
    void whenMapToAvroAndSpecificMapperNotExist_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(ActionTypeProto.UNRECOGNIZED));

        assertThat(e.getMessage(), equalTo("No mapper found for action type UNRECOGNIZED"));
    }
}