package ru.yandex.practicum.kafka.telemetry.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ActionType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ConditionType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceAction;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceType;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public SensorEventAvro mapToAvro(final ClimateSensorEvent event) {
        if (event == null) {
            return null;
        }
        final ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
        return composeSensorEventAvro(event, climateSensorAvro);
    }

    @Override
    public SensorEventAvro mapToAvro(final LightSensorEvent event) {
        if (event == null) {
            return null;
        }
        final LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
        return composeSensorEventAvro(event, lightSensorAvro);
    }

    @Override
    public SensorEventAvro mapToAvro(final MotionSensorEvent event) {
        if (event == null) {
            return null;
        }
        final MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
        return composeSensorEventAvro(event, motionSensorAvro);
    }

    @Override
    public SensorEventAvro mapToAvro(final SwitchSensorEvent event) {
        if (event == null) {
            return null;
        }
        final SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
        return composeSensorEventAvro(event, switchSensorAvro);
    }

    @Override
    public SensorEventAvro mapToAvro(final TemperatureSensorEvent event) {
        if (event == null) {
            return null;
        }
        final TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
        return composeSensorEventAvro(event, temperatureSensorAvro);
    }

    @Override
    public HubEventAvro mapToAvro(final DeviceAddedEvent event) {
        if (event == null) {
            return null;
        }
        final DeviceAddedEventAvro deviceAddedEventAvro = DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapToAvro(event.getDeviceType()))
                .build();
        return composeHubEventAvro(event, deviceAddedEventAvro);
    }

    @Override
    public HubEventAvro mapToAvro(final DeviceRemovedEvent event) {
        if (event == null) {
            return null;
        }
        final DeviceRemovedEventAvro deviceRemovedEventAvro = DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
        return composeHubEventAvro(event, deviceRemovedEventAvro);
    }

    @Override
    public HubEventAvro mapToAvro(final ScenarioAddedEvent event) {
        if (event == null) {
            return null;
        }
        final List<ScenarioConditionAvro> conditions = event.getConditions() == null ? null : event.getConditions()
                .stream()
                .map(this::mapToAvro)
                .collect(Collectors.toCollection(ArrayList::new));
        final List<DeviceActionAvro> actions = event.getActions() == null ? null : event.getActions().stream()
                .map(this::mapToAvro)
                .collect(Collectors.toCollection(ArrayList::new));
        final ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
        return composeHubEventAvro(event, scenarioAddedEventAvro);
    }

    @Override
    public HubEventAvro mapToAvro(final ScenarioRemovedEvent event) {
        if (event == null) {
            return null;
        }
        final ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
        return composeHubEventAvro(event, scenarioRemovedEventAvro);
    }

    private SensorEventAvro composeSensorEventAvro(final SensorEvent event, final Object payload) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private HubEventAvro composeHubEventAvro(final HubEvent event, final Object payload) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private ScenarioConditionAvro mapToAvro(final ScenarioCondition condition) {
        if (condition == null) {
            return null;
        }
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapToAvro(condition.getType()))
                .setOperation(mapToAvro(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    private DeviceActionAvro mapToAvro(final DeviceAction deviceAction) {
        if (deviceAction == null) {
            return null;
        }
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(mapToAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private DeviceTypeAvro mapToAvro(final DeviceType deviceType) {
        if (deviceType == null) {
            return null;
        }
        return switch (deviceType) {
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }

    private ConditionTypeAvro mapToAvro(final ConditionType conditionType) {
        if (conditionType == null) {
            return null;
        }
        return switch (conditionType) {
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case MOTION -> ConditionTypeAvro.MOTION;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
    }

    private ConditionOperationAvro mapToAvro(final ConditionOperation operation) {
        if (operation == null) {
            return null;
        }
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
        };
    }

    private ActionTypeAvro mapToAvro(final ActionType actionType) {
        if (actionType == null) {
            return null;
        }
        return switch (actionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }
}
