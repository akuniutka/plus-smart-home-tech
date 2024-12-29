package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.DeviceAction;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DeviceActionRepository extends JpaRepository<DeviceAction, UUID> {

    List<DeviceAction> findByScenarioIdIn(Collection<UUID> scenarioIds);

    void deleteByScenarioId(UUID scenarioId);
}
