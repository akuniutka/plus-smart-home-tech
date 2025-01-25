package ru.yandex.practicum.commerce.service;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartOperations {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getShoppingCartByUsername(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductsToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Long> products);

    @DeleteMapping("/api/v1/shopping-cart")
    void deactivateShoppingCartByUsername(@RequestParam String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto deleteProductsFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> products);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
            @RequestBody @Valid ChangeProductQuantityRequest request);

    @PostMapping("/api/v1/shopping-cart/booking")
    BookedProductsDto bookProductsInWarehouse(@RequestParam String username);
}
