package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import java.util.UUID;

@Entity
@Table(name = "scenario_conditions")
@Data
@EqualsAndHashCode(of = "id")
public class ScenarioCondition {

    @Id
    private UUID id;

    private UUID scenarioId;
    private String deviceId;

    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro conditionType;

    @Enumerated(EnumType.STRING)
    private ConditionOperationAvro operation;

    private String valueType;
    private String value;
}
