package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events/hubs")
@Slf4j
public class HubEventController {

    private final Map<HubEventType, HubEventHandler> hubEventHandlers;
    private final Clock clock;

    public HubEventController(final Set<HubEventHandler> hubEventHandlers, final Clock clock) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.clock = clock;
    }

    @PostMapping
    public void collect(@Valid @RequestBody HubEvent event) {
        log.debug("Received request with hub event: {}", event);
        if (event.getTimestamp() == null) {
            event.setTimestamp(Instant.now(clock));
        }
        if (hubEventHandlers.containsKey(event.getType())) {
            hubEventHandlers.get(event.getType()).handle(event);
        } else {
            throw new IllegalArgumentException("No handler found for hub event type: " + event.getType());
        }
        log.debug("Responded 200 OK to hub event request");
    }

}
