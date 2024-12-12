package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ActionType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ConditionType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceAction;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEventType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.service.sender.HubEventSender;

import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(final HubEventSender sender) {
        super(sender);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(final HubEvent event) {
        final ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        final List<ScenarioConditionAvro> conditions = _event.getConditions().stream()
                .map(this::mapToAvro)
                .toList();
        final List<DeviceActionAvro> actions = _event.getActions().stream()
                .map(this::mapToAvro)
                .toList();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    protected ScenarioConditionAvro mapToAvro(final ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    protected ConditionTypeAvro mapToAvro(final ConditionType conditionType) {
        return switch (conditionType) {
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case MOTION -> ConditionTypeAvro.MOTION;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
    }

    protected ConditionOperationAvro mapToAvro(final ConditionOperation operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
        };
    }

    protected DeviceActionAvro mapToAvro(final DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    protected ActionTypeAvro mapToAvro(final ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }
}
