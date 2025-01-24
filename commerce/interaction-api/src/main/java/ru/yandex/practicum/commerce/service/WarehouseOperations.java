package ru.yandex.practicum.commerce.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

public interface WarehouseOperations {

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto bookProducts(@RequestBody ShoppingCartDto shoppingCart);
}
