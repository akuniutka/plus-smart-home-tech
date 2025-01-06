package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class BooleanValueConditionMapperTest {

    private static final boolean BOOL_VALUE = true;
    private ConditionTypeMapper mockConditionTypeMapper;
    private OperationMapper mockOperationMapper;

    private BooleanValueConditionMapper mapper;

    private static List<Arguments> provideValuesForPositiveTests() {
        return TestValues.generateValuesForPositiveTests(
                builder -> builder.setBoolValue(BOOL_VALUE),
                builder -> builder.setValue(BOOL_VALUE)
        );
    }

    private static List<ScenarioConditionProto> provideValuesForNegativeTests() {
        return TestValues.generateValuesForNegativeTests(builder -> { });
    }

    @BeforeEach
    void setUp() {
        mockConditionTypeMapper = Mockito.mock(ConditionTypeMapper.class);
        mockOperationMapper = Mockito.mock(OperationMapper.class);
        mapper = new BooleanValueConditionMapper(mockConditionTypeMapper, mockOperationMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockConditionTypeMapper, mockOperationMapper);
    }

    @Test
    void whenGetValueType_ThenReturnBoolValue() {
        final ScenarioConditionProto.ValueCase valueType = mapper.getValueType();

        assertThat(valueType, equalTo(ScenarioConditionProto.ValueCase.BOOL_VALUE));
    }

    @ParameterizedTest
    @MethodSource("provideValuesForPositiveTests")
    void whenMapToAvro_ThenReturnCorrectScenarioConditionAvro(
            final ConditionTypeProto conditionTypeProto,
            final ConditionOperationProto operationProto,
            final ScenarioConditionProto conditionProto,
            final ConditionTypeAvro conditionTypeAvro,
            final ConditionOperationAvro operationAvro,
            final ScenarioConditionAvro expectedConditionAvro
    ) {
        Mockito.when(mockConditionTypeMapper.mapToAvro(any())).thenReturn(conditionTypeAvro);
        Mockito.when(mockOperationMapper.mapToAvro(any())).thenReturn(operationAvro);

        final ScenarioConditionAvro actualConditionAvro = mapper.mapToAvro(conditionProto);

        Mockito.verify(mockConditionTypeMapper).mapToAvro(conditionTypeProto);
        Mockito.verify(mockOperationMapper).mapToAvro(operationProto);
        assertThat(actualConditionAvro, equalTo(expectedConditionAvro));
    }

    @ParameterizedTest
    @MethodSource("provideValuesForNegativeTests")
    void whenMapToAvroAndWrongValueType_ThenThrowException(final ScenarioConditionProto conditionProto) {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(conditionProto));

        assertThat(e.getMessage(), equalTo("Unknown value type: VALUE_NOT_SET"));
    }
}