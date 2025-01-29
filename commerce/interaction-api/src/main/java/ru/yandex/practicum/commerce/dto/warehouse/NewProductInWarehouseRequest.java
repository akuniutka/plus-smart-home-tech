package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private Boolean fragile;

    @NotNull
    @Valid
    private DimensionDto dimension;

    @NotNull
    private BigDecimal weight;
}
