package ru.yandex.practicum.commerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DimensionDto {

    @NotNull
    @Min(1)
    private BigDecimal width;

    @NotNull
    @Min(1)
    private BigDecimal height;

    @NotNull
    @Min(1)
    private BigDecimal depth;
}
