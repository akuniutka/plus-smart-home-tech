package ru.yandex.practicum.kafka.telemetry.mapper;

import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface EventMapper {

    SensorEventAvro mapToAvro(ClimateSensorEvent event);

    SensorEventAvro mapToAvro(LightSensorEvent event);

    SensorEventAvro mapToAvro(MotionSensorEvent event);

    SensorEventAvro mapToAvro(SwitchSensorEvent event);

    SensorEventAvro mapToAvro(TemperatureSensorEvent event);

    HubEventAvro mapToAvro(DeviceAddedEvent event);

    HubEventAvro mapToAvro(DeviceRemovedEvent event);

    HubEventAvro mapToAvro(ScenarioAddedEvent event);

    HubEventAvro mapToAvro(ScenarioRemovedEvent event);
}
