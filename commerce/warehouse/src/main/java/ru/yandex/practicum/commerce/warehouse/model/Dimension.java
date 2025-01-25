package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class Dimension {

    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
}
