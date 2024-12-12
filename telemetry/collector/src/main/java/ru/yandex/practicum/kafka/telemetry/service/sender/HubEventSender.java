package ru.yandex.practicum.kafka.telemetry.service.sender;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventSender {

    void send(HubEventAvro event);
}
