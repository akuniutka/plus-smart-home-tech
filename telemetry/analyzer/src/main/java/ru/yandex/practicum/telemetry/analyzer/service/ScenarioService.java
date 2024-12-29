package ru.yandex.practicum.telemetry.analyzer.service;

import ru.yandex.practicum.telemetry.analyzer.model.Scenario;

import java.util.Collection;

public interface ScenarioService {

    void save(Scenario scenario);

    Collection<Scenario> getByHubId(String hubId);

    void delete(Scenario scenario);
}
