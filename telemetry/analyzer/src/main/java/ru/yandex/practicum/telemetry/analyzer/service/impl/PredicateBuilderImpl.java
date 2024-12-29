package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.evaluator.OperationEvaluator;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.service.ConditionValueExtractorFactory;
import ru.yandex.practicum.telemetry.analyzer.service.PredicateBuilder;
import ru.yandex.practicum.telemetry.analyzer.service.SensorValueExtractorFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class PredicateBuilderImpl implements PredicateBuilder {

    private final Map<ConditionOperationAvro, Map<Class<?>, OperationEvaluator>> evaluators;
    private final ConditionValueExtractorFactory conditionValueExtractorFactory;
    private final SensorValueExtractorFactory sensorValueExtractorFactory;

    public PredicateBuilderImpl(
            final Set<OperationEvaluator> evaluators,
            final ConditionValueExtractorFactory conditionValueExtractorFactory,
            final SensorValueExtractorFactory sensorValueExtractorFactory
    ) {
        this.evaluators = evaluators.stream()
                .collect(Collectors.groupingBy(OperationEvaluator::getOperationType,
                        Collectors.toMap(OperationEvaluator::getOperandType, Function.identity())));
        this.conditionValueExtractorFactory = conditionValueExtractorFactory;
        this.sensorValueExtractorFactory = sensorValueExtractorFactory;
    }

    @Override
    public Predicate<SensorSnapshotAvro> toPredicate(final ScenarioCondition condition) {
        return (snapshot) -> {
            if (!snapshot.getSensorsState().containsKey(condition.getDeviceId())) {
                return false;
            }
            final Object sensorValues = snapshot.getSensorsState().get(condition.getDeviceId()).getData();
            final Object sensorValue =
                    sensorValueExtractorFactory.getExtractor(sensorValues, condition.getConditionType()).get();
            final Object conditionValue = conditionValueExtractorFactory.getExtractor(condition).get();
            if (sensorValue == null || conditionValue == null) {
                return false;
            }
            requireSameType(sensorValue, conditionValue);
            final ConditionOperationAvro operation = condition.getOperation();
            final Class<?> operandType = sensorValue.getClass();
            final OperationEvaluator evaluator = getEvaluator(operation, operandType);
            return evaluator.evaluate(sensorValue, conditionValue);
        };
    }

    private void requireSameType(final Object operandA, final Object operandB) {
        final Class<?> classA = operandA.getClass();
        final Class<?> classB = operandB.getClass();
        if (classA != classB) {
            throw new IllegalArgumentException("Cannot compare %s to %s".formatted(classA, classB));
        }
    }

    private OperationEvaluator getEvaluator(final ConditionOperationAvro operation, final Class<?> operandType) {
        if (!evaluators.containsKey(operation)) {
            throw new IllegalArgumentException("No evaluator for operation " + operation);
        }
        if (!evaluators.get(operation).containsKey(operandType)) {
            throw new IllegalArgumentException("No evaluator for operation %s and operand(s) of type %s"
                    .formatted(operation, operandType));
        }
        return evaluators.get(operation).get(operandType);
    }
 }
