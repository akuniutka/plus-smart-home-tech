package ru.yandex.practicum.kafka.telemetry.service.handler;

import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEventType;

public interface HubEventHandler {

    HubEventType getMessageType();

    void handle(HubEvent event);
}
