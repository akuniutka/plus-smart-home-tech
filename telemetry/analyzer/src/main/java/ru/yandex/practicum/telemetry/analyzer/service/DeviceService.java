package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.telemetry.analyzer.model.Device;

public interface DeviceService {

    void register(Device device);

    void deregister(Device device);
}
