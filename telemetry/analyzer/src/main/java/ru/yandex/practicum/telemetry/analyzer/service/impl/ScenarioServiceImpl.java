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
import ru.yandex.practicum.telemetry.analyzer.repository.HubRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    private static final String SCENARIO_FOR_UNKNOWN_HUB =
            "Cannot save scenario for unknown hub";
    private static final String SCENARIO_HAS_NO_CONDITIONS =
            "Cannot save scenario with no conditions";
    private static final String SCENARIO_HAS_NO_ACTIONS =
            "Cannot save scenario with no actions";
    private static final String SCENARIO_HAS_UNKNOWN_DEVICE =
            "Cannot save scenario with unknown device(s)";
    private static final String DELETE_FROM_UNKNOWN_HUB =
            "Cannot delete scenario from unknown hub";
    private static final String DELETE_UNKNOWN_SCENARIO =
            "Cannot delete unknown scenario";

    private static final Logger log = LoggerFactory.getLogger(ScenarioServiceImpl.class);
    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository conditionRepository;
    private final DeviceActionRepository actionRepository;
    private final DeviceRepository deviceRepository;
    private final HubRepository hubRepository;

    public ScenarioServiceImpl(
            final ScenarioRepository scenarioRepository,
            final ScenarioConditionRepository conditionRepository,
            final DeviceActionRepository actionRepository,
            final DeviceRepository deviceRepository,
            final HubRepository hubRepository
    ) {
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
        this.deviceRepository = deviceRepository;
        this.hubRepository = hubRepository;
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
        log.info("Scenario saved: hubId = {}, name = {}", hubId, name);
        log.debug("Scenario = {}", scenario);
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
        if (!hubRepository.existsById(hubId)) {
            throw new EntityValidationException(DELETE_FROM_UNKNOWN_HUB);
        }
        final Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);
        if (existing.isEmpty()) {
            throw new EntityValidationException(DELETE_UNKNOWN_SCENARIO, "scenario name", name);
        }
        conditionRepository.deleteByScenarioId(existing.get().getId());
        actionRepository.deleteByScenarioId(existing.get().getId());
        scenarioRepository.delete(existing.get());
        log.info("Scenario deleted: hubId = {}, name = {}", hubId, name);
    }

    private void validate(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        if (!hubRepository.existsById(hubId)) {
            throw new EntityValidationException(SCENARIO_FOR_UNKNOWN_HUB);
        }
        validateConditions(scenario);
        validateActions(scenario);
    }

    private void validateConditions(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final Set<String> deviceIds = scenario.getConditions().stream()
                .map(ScenarioCondition::getDeviceId)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) {
            throw new EntityValidationException(SCENARIO_HAS_NO_CONDITIONS);
        }
        validateDevices(hubId, deviceIds);
    }

    private void validateActions(final Scenario scenario) {
        final String hubId = scenario.getHubId();
        final Set<String> deviceIds = scenario.getActions().stream()
                .map(DeviceAction::getDeviceId)
                .collect(Collectors.toSet());
        if (deviceIds.isEmpty()) {
            throw new EntityValidationException(SCENARIO_HAS_NO_ACTIONS);
        }
        validateDevices(hubId, deviceIds);
    }

    private void validateDevices(final String hubId, final Set<String> deviceIds) {
        final Set<String> existingDeviceIds = deviceRepository.findByIdInAndHubId(deviceIds, hubId).stream()
                .map(Device::getId)
                .collect(Collectors.toSet());
        final Set<String> unknownDeviceIds = new HashSet<>(deviceIds);
        unknownDeviceIds.removeAll(existingDeviceIds);
        if (!unknownDeviceIds.isEmpty()) {
            final String unknownDeviceIdsStr = String.join(", ", unknownDeviceIds);
            throw new EntityValidationException(SCENARIO_HAS_UNKNOWN_DEVICE, "deviceId", unknownDeviceIdsStr);
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
