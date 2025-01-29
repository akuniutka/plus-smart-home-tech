package ru.yandex.practicum.commerce.cart.service;

import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCart getShoppingCartByUsername(String username);

    ShoppingCart addProductsToShoppingCart(String username, Map<UUID, Long> products);

    void deactivateShoppingCart(String username);

    ShoppingCart deleteProductsFromShoppingCart(String username, Set<UUID> products);

    ShoppingCart changeProductQuantity(String username, ChangeProductQuantityRequest request);

    BookedProductsDto bookProductsInWarehouse(String username);
}
