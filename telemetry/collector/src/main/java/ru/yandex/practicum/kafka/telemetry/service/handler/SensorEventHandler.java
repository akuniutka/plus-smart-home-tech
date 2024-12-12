package ru.yandex.practicum.kafka.telemetry.service.handler;

import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEventType;

public interface SensorEventHandler {

    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
