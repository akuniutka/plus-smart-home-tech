package ru.yandex.practicum.telemetry.analyzer.dispatcher.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.dispatcher.HubEventDispatcher;
import ru.yandex.practicum.telemetry.analyzer.exception.EntityValidationException;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandlerFactory;

@Component
public class HubEventDispatcherImpl implements HubEventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(HubEventDispatcherImpl.class);
    private final HubEventHandlerFactory hubEventHandlerFactory;

    public HubEventDispatcherImpl(final HubEventHandlerFactory hubEventHandlerFactory) {
        this.hubEventHandlerFactory = hubEventHandlerFactory;
    }

    public void dispatch(final HubEventAvro event) {
        final String payloadType = event.getPayload().getClass().getName();
        log.info("Received hub event: hubId = {}, timestamp = {}, payload type = {}", event.getHubId(),
                event.getTimestamp(), payloadType);
        log.debug("Hub event = {}", event);
        final HubEventHandler handler = hubEventHandlerFactory.getHandler(payloadType);
        try {
            handler.handle(event);
        } catch (EntityValidationException e) {
            final String extraInfo = e.getParameterName() == null ? "" :
                    ", %s = %s".formatted(e.getParameterName(), e.getParameterValue());
            log.warn("{}: hubId = {}, timestamp = {}, payload type = {}{}", e.getMessage(), event.getHubId(),
                    event.getTimestamp(), payloadType, extraInfo);
        }
    }
}
