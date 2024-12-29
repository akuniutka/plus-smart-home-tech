package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.analyzer.exception.EntityValidationException;
import ru.yandex.practicum.telemetry.analyzer.model.Device;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.HubRepository;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceService;
import ru.yandex.practicum.telemetry.analyzer.service.HubService;

import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String REGISTER_WITH_UNKNOWN_HUB =
            "Cannot register device [%s] with hub [%s]: unknown hub";
    private static final String REGISTERED_WITH_ANOTHER_HUB =
            "Cannot register device [%s] with hub [%s]: already registered with different hub";
    private static final String REGISTERED_WITH_ANOTHER_TYPE =
            "Cannot register Device [%s] with type of [%s]: already registered with different type";
    private static final String REGISTER_DUPLICATE =
            "Device [%s] already registered";
    private static final String DEREGISTER_FROM_UNKNOWN_HUB =
            "Cannot deregister device [%s] from hub [%s]: unknown hub";
    private static final String DEREGISTER_UNKNOWN_DEVICE =
            "Cannot deregister device [%s] from hub [%s]: unknown device";

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
        final Optional<Device> registered = deviceRepository.findById(device.getId());
        if (registered.isEmpty()) {
            registerInternally(device);
        } else {
            checkForDuplicate(registered.get(), device);
        }
    }

    @Override
    public void deregister(final Device device) {
        final String hubId = device.getHubId();
        final String id = device.getId();
        if (!hubRepository.existsById(hubId)) {
            throw new EntityValidationException(DEREGISTER_FROM_UNKNOWN_HUB.formatted(id, hubId));
        }
        final Optional<Device> registered = deviceRepository.findByIdAndHubId(id, hubId);
        if (registered.isEmpty()) {
            throw new EntityValidationException(DEREGISTER_UNKNOWN_DEVICE.formatted(id, hubId));
        }
        deviceRepository.delete(device);
        log.info("Device [{}] deregistered from hub [{}]", id, hubId);
    }

    private void registerInternally(final Device device) {
        final String hubId = device.getHubId();
        final String id = device.getId();
        if (!hubService.existsById(hubId)) {
            throw new EntityValidationException(REGISTER_WITH_UNKNOWN_HUB.formatted(id, hubId));
        }
        deviceRepository.save(device);
        log.info("Device [{}] registered with hub [{}]", id, hubId);
    }

    private void checkForDuplicate(final Device registered, final Device device) {
        final String hubId = device.getHubId();
        final String id = device.getId();
        final DeviceTypeAvro type = device.getType();
        if (!registered.getHubId().equals(hubId)) {
            throw new EntityValidationException(REGISTERED_WITH_ANOTHER_HUB.formatted(id, hubId));
        } else if (!registered.getType().equals(type)) {
            throw new EntityValidationException(REGISTERED_WITH_ANOTHER_TYPE.formatted(id, type));
        } else {
            throw new EntityValidationException(REGISTER_DUPLICATE.formatted(id));
        }
    }
}
