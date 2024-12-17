package ru.yandex.practicum.telemetry.collector.service.sender;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventSender {

    void send(HubEventAvro event);
}
