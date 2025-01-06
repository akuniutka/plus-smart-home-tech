package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapperFactory;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionMapperFactoryImplTest {

    private static List<ScenarioConditionProto.ValueCase> valueTypes;
    private static List<ConditionMapper> mockMappers;

    private ConditionMapperFactory factory;

    @BeforeAll
    static void init() {
        valueTypes = TestModels.getPossibleConditionValueTypes();
        mockMappers = new ArrayList<>();
        for (ScenarioConditionProto.ValueCase valueType : valueTypes) {
            ConditionMapper mockMapper = Mockito.mock(ConditionMapper.class);
            Mockito.when(mockMapper.getValueType()).thenReturn(valueType);
            mockMappers.add(mockMapper);
        }
    }

    private static List<Arguments> provideTestValues() {
        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < valueTypes.size(); i++) {
            testValues.add(Arguments.of(valueTypes.get(i), mockMappers.get(i)));
        }
        return testValues;
    }

    @BeforeEach
    void setUp() {
        mockMappers.forEach(Mockito::clearInvocations);
        factory = new ConditionMapperFactoryImpl(new HashSet<>(mockMappers));
    }

    @AfterEach
    void tearDown() {
        for (ConditionMapper mockMapper : mockMappers) {
            Mockito.verify(mockMapper).getValueType();
            Mockito.verifyNoMoreInteractions(mockMapper);
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestValues")
    void whenGetMapperAndMapperExist_ThenReturnCorrectMapper(final ScenarioConditionProto.ValueCase valueType,
            final ConditionMapper expectedMapper) {
        final ConditionMapper actualMapper = factory.getMapper(valueType);

        assertThat(actualMapper, sameInstance(expectedMapper));
    }

    @Test
    void whenGetMapperAndMapperNotExist_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> factory.getMapper(null));

        assertThat(e.getMessage(), equalTo("No condition mapper found for value type null"));
    }
}