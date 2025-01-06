package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventService {

    void addToProcessingQueue(SensorEventAvro event);
}
