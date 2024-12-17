package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events/sensors")
@Slf4j
public class SensorEventController {

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Clock clock;

    public SensorEventController(final Set<SensorEventHandler> sensorEventHandlers, final Clock clock) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.clock = clock;
    }

    @PostMapping
    public void collect(@Valid @RequestBody SensorEvent event) {
        log.debug("Received request with sensor event: {}", event);
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now(clock));
        }
        if (sensorEventHandlers.containsKey(event.getType())) {
            sensorEventHandlers.get(event.getType()).handle(event);
        } else {
            throw new IllegalArgumentException("No handler found for sensor event type: " + event.getType());
        }
        log.debug("Responded 200 OK to sensor event request");
    }
}
