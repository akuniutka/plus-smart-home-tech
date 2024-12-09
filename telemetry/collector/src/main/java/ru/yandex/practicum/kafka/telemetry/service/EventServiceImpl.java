package ru.yandex.practicum.kafka.telemetry.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioAddedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.mapper.EventMapper;
import ru.yandex.practicum.kafka.telemetry.util.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.util.KafkaSender;

import java.time.Clock;
import java.time.Instant;

@Service
@Validated
@Slf4j
public class EventServiceImpl implements EventService {

    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    private final EventMapper mapper;
    private final KafkaSender sender;
    private final Clock clock;

    public EventServiceImpl(final EventMapper mapper, final KafkaClient client, final Clock clock) {
        this.mapper = mapper;
        this.sender = client.getSender();
        this.clock = clock;
    }

    @Override
    public void collectSensorEvent(final SensorEvent event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now(clock));
        }
        final SensorEventAvro sensorEventAvro =
                switch (event) {
                    case ClimateSensorEvent climateSensorEvent -> mapper.mapToAvro(climateSensorEvent);
                    case LightSensorEvent lightSensorEvent -> mapper.mapToAvro(lightSensorEvent);
                    case MotionSensorEvent motionSensorEvent -> mapper.mapToAvro(motionSensorEvent);
                    case SwitchSensorEvent switchSensorEvent -> mapper.mapToAvro(switchSensorEvent);
                    case TemperatureSensorEvent temperatureSensorEvent -> mapper.mapToAvro(temperatureSensorEvent);
                    case null, default -> throw new AssertionError();
                };
        sender.send(SENSOR_TOPIC, sensorEventAvro);
        log.debug("Sent sensor event to topic [{}]: {}", SENSOR_TOPIC, sensorEventAvro);
    }

    @Override
    public void collectHubEvent(final HubEvent event) {
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now(clock));
        }
        final HubEventAvro hubEventAvro =
                switch (event) {
                    case DeviceAddedEvent deviceAddedEvent -> mapper.mapToAvro(deviceAddedEvent);
                    case DeviceRemovedEvent deviceRemovedEvent -> mapper.mapToAvro(deviceRemovedEvent);
                    case ScenarioAddedEvent scenarioAddedEvent -> mapper.mapToAvro(scenarioAddedEvent);
                    case ScenarioRemovedEvent scenarioRemovedEvent -> mapper.mapToAvro(scenarioRemovedEvent);
                    case null, default -> throw new AssertionError();
                };
        sender.send(HUB_TOPIC, hubEventAvro);
        log.debug("Sent hub event to topic [{}]: {}", HUB_TOPIC, hubEventAvro);
    }
}
