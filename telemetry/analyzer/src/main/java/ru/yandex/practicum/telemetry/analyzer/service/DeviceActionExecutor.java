package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

public interface DeviceActionExecutor {

    void execute(Scenario scenario);
}
