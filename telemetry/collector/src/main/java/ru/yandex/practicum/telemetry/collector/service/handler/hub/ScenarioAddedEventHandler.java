package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.service.sender.HubEventSender;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(final HubEventSender sender) {
        super(sender);
    }

    @Override
    public HubEventProto.PayloadCase getPayloadType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapPayload(final HubEventProto event) {
        final ScenarioAddedEventProto payload = event.getScenarioAdded();
        final List<ScenarioConditionAvro> conditions = payload.getConditionList().stream()
                .map(this::mapToAvro)
                .toList();
        final List<DeviceActionAvro> actions = payload.getActionList().stream()
                .map(this::mapToAvro)
                .toList();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(payload.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    protected ScenarioConditionAvro mapToAvro(final ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(extractValue(condition))
                .build();
    }

    protected ConditionTypeAvro mapToAvro(final ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case MOTION -> ConditionTypeAvro.MOTION;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            default -> throw new IllegalArgumentException("Unknown scenario condition type " + conditionType);
        };
    }

    protected ConditionOperationAvro mapToAvro(final ConditionOperationProto operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Unknown scenario operation " + operation);
        };
    }

    protected Object extractValue(final ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case INT_VALUE -> condition.getIntValue();
            case BOOL_VALUE -> condition.getBoolValue();
            case VALUE_NOT_SET -> null;
        };
    }

    protected DeviceActionAvro mapToAvro(final DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToAvro(deviceAction.getType()))
                .setValue(deviceAction.hasValue() ? deviceAction.getValue() : null)
                .build();
    }

    protected ActionTypeAvro mapToAvro(final ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Unknown action type " + actionType);
        };
    }
}
