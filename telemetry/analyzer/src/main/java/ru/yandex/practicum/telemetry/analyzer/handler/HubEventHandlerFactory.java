package ru.yandex.practicum.telemetry.analyzer.handler;

public interface HubEventHandlerFactory {

    HubEventHandler getHandler(String payloadType);
}
