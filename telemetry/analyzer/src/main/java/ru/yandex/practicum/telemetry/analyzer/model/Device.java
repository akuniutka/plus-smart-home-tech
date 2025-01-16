package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Entity
@Table(name = "devices", schema = "analyzer")
@Data
@EqualsAndHashCode(of = "id")
public class Device {

    @Id
    private String id;

    private String hubId;

    @Enumerated(EnumType.STRING)
    private DeviceTypeAvro type;
}
