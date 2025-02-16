package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartOperations {

    @GetMapping
    ShoppingCartDto getShoppingCartByUsername(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductsToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivateShoppingCartByUsername(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto deleteProductsFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam String username,
            @RequestBody @Valid ChangeProductQuantityRequest request);

    @PostMapping("/booking")
    BookedProductsDto bookProductsInWarehouse(@RequestParam String username);
}
