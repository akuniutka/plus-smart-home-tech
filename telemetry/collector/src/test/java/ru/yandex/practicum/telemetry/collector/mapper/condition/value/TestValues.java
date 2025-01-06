package ru.yandex.practicum.telemetry.collector.mapper.condition.value;

import org.junit.jupiter.params.provider.Arguments;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ru.yandex.practicum.telemetry.collector.util.TestModels.getPossibleConditionTypes;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getPossibleOperations;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestScenarioConditionProto;

public final class TestValues {

    private TestValues() {
    }

    /**
     * Creates test data for all allowed combinations of condition type and operation. Uses
     * <code>valueProviderProto</code> and <code>valueProviderAvro</code> to generate test pairs of
     * <code>ScenarioConditionProto</code> and <code>ScenarioConditionAvro</code> for a specific condition
     * mapper.
     *
     * @param valueProviderProto provider of value for generated <code>ScenarioConditionProto</code>
     * @param valueProviderAvro  provider of value for generated <code>ScenarioConditionAvro</code>
     * @return list of combinations of <code>ConditionTypeProto</code>, <code>ConditionOperationProto</code>,
     * <code>ScenarioConditionProto</code>, <code>ConditionTypeAvro</code>, <code>ConditionOperationAvro</code>,
     * <code>ScenarioConditionAvro</code>
     */
    public static List<Arguments> generateValuesForPositiveTests(
            final Consumer<ScenarioConditionProto.Builder> valueProviderProto,
            final Consumer<ScenarioConditionAvro.Builder> valueProviderAvro
    ) {
        final List<ConditionTypeProto> conditionTypesProto = TestModels.getPossibleConditionTypes();
        final List<ConditionOperationProto> operationsProto = TestModels.getPossibleOperations();
        final List<ConditionTypeAvro> conditionTypesAvro = TestModels.getTestConditionTypesAvro();
        final List<ConditionOperationAvro> operationsAvro = TestModels.getTestOperationsAvro();

        final List<Arguments> testValues = new ArrayList<>();
        for (int i = 0; i < conditionTypesProto.size(); i++) {
            for (int j = 0; j < operationsProto.size(); j++) {
                ConditionTypeProto conditionTypeProto = conditionTypesProto.get(i);
                ConditionOperationProto operationProto = operationsProto.get(j);
                ConditionTypeAvro conditionTypeAvro = conditionTypesAvro.get(i);
                ConditionOperationAvro operationAvro = operationsAvro.get(j);
                testValues.add(Arguments.of(
                        conditionTypeProto,
                        operationProto,
                        TestModels.getTestScenarioConditionProto(conditionTypeProto, operationProto,
                                valueProviderProto),
                        conditionTypeAvro,
                        operationAvro,
                        TestModels.getTestScenarioConditionAvro(conditionTypeAvro, operationAvro, valueProviderAvro)
                ));
            }
        }
        return testValues;
    }

    /**
     * Creates a list of scenarios to be used in negative test cases for a specific condition mapper.
     * Uses <code>valueProviderProto</code> as provider of *wrong* value and cover all allowed combinations
     * of condition type and operation.
     *
     * @param valueProvider provider of wrong value for generated <code>ScenarioConditionProto</code>
     * @return list of <code>ConditionTypeProto</code>
     */
    public static List<ScenarioConditionProto> generateValuesForNegativeTests(
            final Consumer<ScenarioConditionProto.Builder> valueProvider
    ) {
        return getPossibleConditionTypes().stream()
                .flatMap(conditionType -> getPossibleOperations().stream()
                        .map(operation -> getTestScenarioConditionProto(conditionType, operation,
                                valueProvider)))
                .toList();
    }
}
