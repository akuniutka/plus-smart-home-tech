package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "hubs", schema = "analyzer")
@Data
@EqualsAndHashCode(of = "id")
public class Hub {

    @Id
    private String id;
}
