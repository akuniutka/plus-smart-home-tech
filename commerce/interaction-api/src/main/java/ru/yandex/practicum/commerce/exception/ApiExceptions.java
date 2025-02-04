package ru.yandex.practicum.commerce.exception;

import java.util.Map;

public final class ApiExceptions {

    public static final String API_EXCEPTION_HEADER = "X-API-Exception";
    public static final Map<String, Class<? extends ApiException>> EXCEPTIONS = Map.of(
            "NoOrderFoundException", NoOrderFoundException.class,
            "NoProductsInShoppingCartException", NoProductsInShoppingCartException.class,
            "NoSpecifiedProductInWarehouseException", NoSpecifiedProductInWarehouseException.class,
            "NotAuthorizedUserException", NotAuthorizedUserException.class,
            "ProductInShoppingCartLowQuantityInWarehouse", ProductInShoppingCartLowQuantityInWarehouse.class,
            "ProductInShoppingCartNotInWarehouse", ProductInShoppingCartNotInWarehouse.class,
            "ProductNotFoundException", ProductNotFoundException.class,
            "ShoppingCartDeactivatedException", ShoppingCartDeactivatedException.class,
            "SpecifiedProductAlreadyInWarehouseException", SpecifiedProductAlreadyInWarehouseException.class
    );

    private ApiExceptions() {
        throw new AssertionError();
    }
}
