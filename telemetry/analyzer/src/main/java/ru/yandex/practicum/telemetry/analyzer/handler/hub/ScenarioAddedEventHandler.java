package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.model.DeviceAction;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    private final ScenarioService service;

    public ScenarioAddedEventHandler(final ScenarioService service) {
        this.service = service;
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Override
    protected ScenarioAddedEventAvro cast(final Object payload) {
        return (ScenarioAddedEventAvro) payload;
    }

    @Override
    protected void handleInternally(final String hubId, final ScenarioAddedEventAvro payload) {
        final Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(payload.getName());
        scenario.setConditions(payload.getConditions().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toCollection(ArrayList::new)));
        scenario.setActions(payload.getActions().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toCollection(ArrayList::new)));
        service.save(scenario);
    }

    private ScenarioCondition mapToEntity(final ScenarioConditionAvro condition) {
        final Object value = condition.getValue();
        final ScenarioCondition _condition = new ScenarioCondition();
        _condition.setDeviceId(condition.getSensorId());
        _condition.setConditionType(condition.getType());
        _condition.setOperation(condition.getOperation());
        if (value != null) {
            _condition.setValueType(value.getClass().getSimpleName());
            _condition.setValue(value.toString());
        }
        return _condition;
    }

    private DeviceAction mapToEntity(final DeviceActionAvro action) {
        final DeviceAction _action = new DeviceAction();
        _action.setDeviceId(action.getSensorId());
        _action.setType(action.getType());
        _action.setValue(action.getValue());
        return _action;
    }
}
