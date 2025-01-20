package ru.yandex.practicum.commerce.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {

    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    @NotNull
    @Digits(integer = 1, fraction = 1)
    @Min(1)
    @Max(5)
    private BigDecimal rating;

    private ProductCategory productCategory;

    @NotNull
    @Digits(integer = 17, fraction = 2)
    private BigDecimal price;
}
