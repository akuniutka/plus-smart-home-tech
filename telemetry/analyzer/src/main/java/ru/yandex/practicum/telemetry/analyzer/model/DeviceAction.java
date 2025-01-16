package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import java.util.UUID;

@Entity
@Table(name = "device_actions", schema = "analyzer")
@Data
@EqualsAndHashCode(of = "id")
public class DeviceAction {

    @Id
    private UUID id;

    private UUID scenarioId;
    private String deviceId;

    @Enumerated(EnumType.STRING)
    private ActionTypeAvro type;

    private Integer value;
}
