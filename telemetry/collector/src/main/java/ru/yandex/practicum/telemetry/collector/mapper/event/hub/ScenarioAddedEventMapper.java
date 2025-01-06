package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ActionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapperFactory;

import java.util.List;

@Component
public class ScenarioAddedEventMapper extends BaseHubEventMapper<ScenarioAddedEventAvro> {

    protected final ConditionMapperFactory conditionMapperFactory;
    protected final ActionTypeMapper actionTypeMapper;

    public ScenarioAddedEventMapper(
            final ConditionMapperFactory conditionMapperFactory,
            final ActionTypeMapper actionTypeMapper
    ) {
        this.conditionMapperFactory = conditionMapperFactory;
        this.actionTypeMapper = actionTypeMapper;
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
        final ConditionMapper conditionMapper = conditionMapperFactory.getMapper(condition.getValueCase());
        return conditionMapper.mapToAvro(condition);
    }

    protected DeviceActionAvro mapToAvro(final DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(actionTypeMapper.mapToAvro(deviceAction.getType()))
                .setValue(deviceAction.hasValue() ? deviceAction.getValue() : null)
                .build();
    }
}
