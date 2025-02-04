package ru.yandex.practicum.commerce.cart.service;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;

@FeignClient(name = "warehouse")
public interface WarehouseService {

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductsAvailability(@RequestBody @Valid ShoppingCartDto shoppingCart);
}
