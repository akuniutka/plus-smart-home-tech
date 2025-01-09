package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.exception.EntityValidationException;
import ru.yandex.practicum.telemetry.analyzer.model.Device;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.HubRepository;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceService;
import ru.yandex.practicum.telemetry.analyzer.service.HubService;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String REGISTER_WITH_UNKNOWN_HUB =
            "Cannot register device with unknown hub";
    private static final String REGISTERED_WITH_ANOTHER_HUB =
            "Cannot register device already registered with another hub";
    private static final String REGISTERED_WITH_ANOTHER_TYPE =
            "Cannot register device already registered with another type";
    private static final String REGISTER_DUPLICATE =
            "Device already registered";
    private static final String DEREGISTER_FROM_UNKNOWN_HUB =
            "Cannot deregister device from unknown hub";
    private static final String DEREGISTER_UNKNOWN_DEVICE =
            "Cannot deregister unknown device";

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);
    private final HubService hubService;
    private final DeviceRepository deviceRepository;
    private final HubRepository hubRepository;

    public DeviceServiceImpl(
            final HubService hubService,
            final DeviceRepository deviceRepository,
            final HubRepository hubRepository
    ) {
        this.hubService = hubService;
        this.deviceRepository = deviceRepository;
        this.hubRepository = hubRepository;
    }

    @Override
    public void register(final Device device) {
        validate(device);
        deviceRepository.save(device);
        log.info("Device registered: hubId = {}, deviceId = {}", device.getHubId(), device.getId());
        log.debug("Device = {}", device);
    }

    @Override
    public void deregister(final Device device) {
        final String hubId = device.getHubId();
        final String id = device.getId();
        if (!hubRepository.existsById(hubId)) {
            throw new EntityValidationException(DEREGISTER_FROM_UNKNOWN_HUB);
        }
        deviceRepository.findByIdAndHubId(id, hubId).orElseThrow(
                () -> new EntityValidationException(DEREGISTER_UNKNOWN_DEVICE, "deviceId", id)
        );
        deviceRepository.delete(device);
        log.info("Device deregistered: hubId = {}, deviceId = {}", hubId, id);
    }

    private void validate(Device device) {
        if (!hubService.existsById(device.getHubId())) {
            throw new EntityValidationException(REGISTER_WITH_UNKNOWN_HUB);
        }
        deviceRepository.findById(device.getId()).ifPresent(
                existing -> {
                    if (!existing.getHubId().equals(device.getHubId())) {
                        throw new EntityValidationException(REGISTERED_WITH_ANOTHER_HUB, "another hub id",
                                existing.getHubId());
                    } else if (!existing.getType().equals(device.getType())) {
                        throw new EntityValidationException(REGISTERED_WITH_ANOTHER_TYPE, "registered type",
                                existing.getType().toString());
                    } else {
                        throw new EntityValidationException(REGISTER_DUPLICATE);
                    }
                }
        );
    }
}
