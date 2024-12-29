package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.ScenarioCondition;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, UUID> {

    List<ScenarioCondition> findByScenarioIdIn(Collection<UUID> scenarioIds);

    void deleteByScenarioId(UUID scenarioId);
}
