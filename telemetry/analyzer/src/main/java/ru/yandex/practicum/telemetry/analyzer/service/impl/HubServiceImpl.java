package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.model.Hub;
import ru.yandex.practicum.telemetry.analyzer.repository.HubRepository;
import ru.yandex.practicum.telemetry.analyzer.service.HubService;

@Service
public class HubServiceImpl implements HubService {

    private final HubRepository repository;

    public HubServiceImpl(final HubRepository repository) {
        this.repository = repository;
    }

    /**
     * Stub method which always returns true to request if specified hub exists.
     * Will be replaced with real code later.
     *
     */
    @Override
    public boolean existsById(final String id) {
        if (!repository.existsById(id)) {
            final Hub hub = new Hub();
            hub.setId(id);
            repository.save(hub);
        }
        return true;
    }
}
