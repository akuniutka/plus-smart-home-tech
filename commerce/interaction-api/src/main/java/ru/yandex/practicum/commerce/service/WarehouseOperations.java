package ru.yandex.practicum.commerce.service;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

public interface WarehouseOperations {

    @PutMapping("/api/v1/warehouse")
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto bookProducts(@RequestBody @Valid ShoppingCartDto shoppingCart);

    @PostMapping("/api/v1/warehouse/add")
    void increaseProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();
}
