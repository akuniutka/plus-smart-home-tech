package ru.yandex.practicum.kafka.telemetry.service.sender;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface SensorEventSender {

    void send(SensorEventAvro event);
}
