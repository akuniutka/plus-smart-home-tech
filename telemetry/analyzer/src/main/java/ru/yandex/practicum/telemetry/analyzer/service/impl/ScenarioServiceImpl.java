package ru.yandex.practicum.telemetry.analyzer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.exception.EntityValidationException;
import ru.yandex.practicum.telemetry.analyzer.model.Device;
import ru.yandex.practicum.telemetry.analyzer.model.DeviceAction;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.DeviceRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    private static final String SCENARIO_HAS_NO_CONDITIONS =
            "Cannot save scenario [%s] for hub [%s]: scenario has no conditions";
    private static final String SCENARIO_HAS_NO_ACTIONS =
            "Cannot save scenario [%s] for hub [%s]: scenario has no actions";
    private static final String SCENARIO_HAS_UNKNOWN_DEVICE =
            "Cannot save scenario [%s] for hub [%s]: unknown device(s) %s";
    private static final String UNKNOWN_SCENARIO =
            "Cannot delete scenario [%s] for hub [%s]: unknown scenario";

    private static final Logger log = LoggerFactory.getLogger(ScenarioServiceImpl.class);
    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository conditionRepository;
    private final DeviceActionRepository actionRepository;
    private final DeviceRepository deviceRepository;

    public ScenarioServiceImpl(
            final ScenarioRepository scenarioRepository,
            final ScenarioConditionRepository conditionRepository,
            final DeviceActionRepository actionRepository,
            final DeviceRepository deviceRepository
    ) {
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    @Override
    public void save(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final String name = scenario.getName();
        validate(scenario);
        final Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);
        if (existing.isEmpty()) {
            scenario.setId(UUID.randomUUID());
            scenarioRepository.save(scenario);
        } else {
            scenario.setId(existing.get().getId());
            conditionRepository.deleteByScenarioId(scenario.getId());
            actionRepository.deleteByScenarioId(scenario.getId());
        }
        populateScenarioIdToConditionsAndActions(scenario);
        saveConditions(scenario);
        saveActions(scenario);
        log.info("Scenario [{}] for hub [{}] saved", name, hubId);
    }

    @Override
    public Collection<Scenario> getByHubId(final String hubId) {
        final Map<UUID, Scenario> scenarios = scenarioRepository.findByHubId(hubId).stream()
                .collect(Collectors.toMap(Scenario::getId, Function.identity()));
        scenarios.values().forEach(scenario -> {
                    scenario.setConditions(new ArrayList<>());
                    scenario.setActions(new ArrayList<>());
                });
        final Collection<UUID> scenarioIds = scenarios.keySet();
        conditionRepository.findByScenarioIdIn(scenarioIds).forEach(
                condition -> scenarios.get(condition.getScenarioId()).getConditions().add(condition)
        );
        actionRepository.findByScenarioIdIn(scenarioIds).forEach(
                action -> scenarios.get(action.getScenarioId()).getActions().add(action)
        );
        return scenarios.values();
    }

    @Transactional
    @Override
    public void delete(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final String name = scenario.getName();
        final Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);
        if (existing.isEmpty()) {
            throw new EntityValidationException(UNKNOWN_SCENARIO.formatted(name, hubId));
        }
        conditionRepository.deleteByScenarioId(existing.get().getId());
        actionRepository.deleteByScenarioId(existing.get().getId());
        scenarioRepository.delete(existing.get());
        log.info("Scenario [{}] for hub [{}] deleted", name, hubId);
    }

    private void validate(final Scenario scenario) {
        validateConditions(scenario);
        validateActions(scenario);
    }

    private void validateConditions(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final String name = scenario.getName();
        final Set<String> deviceIds = scenario.getConditions().stream()
                .map(ScenarioCondition::getDeviceId)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) {
            throw new EntityValidationException(SCENARIO_HAS_NO_CONDITIONS.formatted(name, hubId));
        }
        deviceRepository.findByIdInAndHubId(deviceIds, hubId).stream()
                .map(Device::getId)
                .forEach(deviceIds::remove);
        if (!deviceIds.isEmpty()) {
            final String deviceIdsStr = String.join(", ", deviceIds);
            throw new EntityValidationException(SCENARIO_HAS_UNKNOWN_DEVICE.formatted(name, hubId, deviceIdsStr));
        }
    }

    private void validateActions(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final String name = scenario.getName();
        final Set<String> deviceIds = scenario.getActions().stream()
                .map(DeviceAction::getDeviceId)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) {
            throw new EntityValidationException(SCENARIO_HAS_NO_ACTIONS.formatted(name, hubId));
        }
        deviceRepository.findByIdInAndHubId(deviceIds, hubId).stream()
                .map(Device::getId)
                .forEach(deviceIds::remove);
        if (!deviceIds.isEmpty()) {
            final String deviceIdsStr = String.join(", ", deviceIds);
            throw new EntityValidationException(SCENARIO_HAS_UNKNOWN_DEVICE.formatted(name, hubId, deviceIdsStr));
        }
    }

    private void populateScenarioIdToConditionsAndActions(final Scenario scenario) {
        scenario.getConditions().forEach(condition -> condition.setScenarioId(scenario.getId()));
        scenario.getActions().forEach(action -> action.setScenarioId(scenario.getId()));
    }

    private void saveConditions(final Scenario scenario) {
        scenario.getConditions().forEach(condition -> condition.setId(UUID.randomUUID()));
        conditionRepository.saveAll(scenario.getConditions());
    }

    private void saveActions(final Scenario scenario) {
        scenario.getActions().forEach(action -> action.setId(UUID.randomUUID()));
        actionRepository.saveAll(scenario.getActions());
    }
}
