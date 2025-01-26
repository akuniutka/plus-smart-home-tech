package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "warehouse")
@Data
@EqualsAndHashCode(of = "productId")
public class Product {

    @Id
    private UUID productId;

    private Boolean fragile;

    @Embedded
    private Dimension dimension;

    private BigDecimal weight;
    private long totalQuantity;
    private long bookedQuantity;
}
