package ru.yandex.practicum.telemetry.analyzer.handler.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandlerFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HubEventHandlerFactoryImpl implements HubEventHandlerFactory {

    private final Map<String, HubEventHandler> handlers;

    public HubEventHandlerFactoryImpl(final Set<HubEventHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getPayloadType, Function.identity()));
    }

    @Override
    public HubEventHandler getHandler(final String payloadType) {
        if (!handlers.containsKey(payloadType)) {
            throw new IllegalArgumentException("No hub event handler found for payload type " + payloadType);
        }
        return handlers.get(payloadType);
    }
}
