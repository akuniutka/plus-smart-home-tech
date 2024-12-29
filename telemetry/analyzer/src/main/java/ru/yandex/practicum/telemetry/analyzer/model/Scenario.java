package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "scenarios")
@Data
@EqualsAndHashCode(of = "id")
public class Scenario {

    @Id
    private UUID id;

    private String hubId;
    private String name;

    @Transient
    private List<ScenarioCondition> conditions;

    @Transient
    private List<DeviceAction> actions;
}
