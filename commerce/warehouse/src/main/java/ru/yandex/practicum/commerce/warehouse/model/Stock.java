package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "stocks", schema = "warehouse")
@Data
@EqualsAndHashCode(of = "productId")
public class Stock {

    @Id
    private UUID productId;

    private long totalQuantity;
    private long bookedQuantity;
}
