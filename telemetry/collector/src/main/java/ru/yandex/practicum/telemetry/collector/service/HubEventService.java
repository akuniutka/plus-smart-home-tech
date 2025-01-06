package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {

    void addToProcessingQueue(HubEventAvro event);
}
