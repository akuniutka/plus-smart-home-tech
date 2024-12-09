package ru.yandex.practicum.kafka.telemetry.service;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;

public interface EventService {

    void collectSensorEvent(@NotNull SensorEvent event);

    void collectHubEvent(@NotNull HubEvent event);
}
