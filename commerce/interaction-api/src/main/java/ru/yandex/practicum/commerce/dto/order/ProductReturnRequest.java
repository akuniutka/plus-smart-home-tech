package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ProductReturnRequest {

    private UUID orderId;

    @NotNull
    private Map<UUID, Long> products;
}
