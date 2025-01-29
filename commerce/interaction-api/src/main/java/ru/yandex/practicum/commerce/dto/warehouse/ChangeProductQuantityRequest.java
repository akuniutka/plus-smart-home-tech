package ru.yandex.practicum.commerce.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Long newQuantity;
}
