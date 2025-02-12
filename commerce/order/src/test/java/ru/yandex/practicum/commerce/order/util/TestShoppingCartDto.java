package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public class TestShoppingCartDto {

    public static final UUID SHOPPING_CART_ID = UUID.fromString("801b5a89-c5f1-435c-a54e-d06cd6662a6a");
    public static final Map<UUID, Long> PRODUCTS = Map.of(
            UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25"), 1L,
            UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808"), 2L
    );

    public static final UUID SHOPPING_CART_ID_OTHER = UUID.fromString("3771b0ec-41ec-4c28-80e1-d28b7092df8c");
    public static final Map<UUID, Long> PRODUCTS_OTHER = Map.of(
            UUID.fromString("c504066c-e949-46c6-b6b3-31a9bdcb3176"), 7L,
            UUID.fromString("dbe1ac64-08b7-4209-a877-21caa44ede79"), 8L
    );

    private TestShoppingCartDto() {
        throw new AssertionError();
    }

    public static ShoppingCartDto create() {
        final ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(SHOPPING_CART_ID);
        dto.setProducts(PRODUCTS);
        return dto;
    }
}
