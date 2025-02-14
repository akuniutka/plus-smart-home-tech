package ru.yandex.practicum.commerce.order.service;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseService {

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductsAvailability(@RequestBody @Valid ShoppingCartDto shoppingCart);

    @PostMapping("/api/v1/warehouse/return")
    void returnProducts(@RequestBody Map<UUID, Long> products);
}
