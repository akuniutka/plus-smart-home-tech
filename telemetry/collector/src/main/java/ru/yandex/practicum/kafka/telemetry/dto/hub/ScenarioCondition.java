package ru.yandex.practicum.kafka.telemetry.dto.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarioCondition {

    @NotBlank
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private ConditionOperation operation;

    @NotNull
    private Integer value;

    public enum ConditionType {
        CO2LEVEL,
        HUMIDITY,
        LUMINOSITY,
        MOTION,
        SWITCH,
        TEMPERATURE
    }

    public enum ConditionOperation {
        EQUALS,
        GREATER_THAN,
        LOWER_THAN
    }
}
