package ru.yandex.practicum.commerce.exception;

import java.util.HashMap;
import java.util.Map;

public final class ApiExceptions {

    public static final String API_EXCEPTION_HEADER = "X-API-Exception";
    public static final Map<String, Class<? extends ApiException>> EXCEPTIONS = registerApiExceptions();

    private ApiExceptions() {
        throw new AssertionError();
    }

    private static Map<String, Class<? extends ApiException>> registerApiExceptions() {
        final Map<String, Class<? extends ApiException>> exceptions = new HashMap<>();
        exceptions.put("NoDeliveryFoundException", NoDeliveryFoundException.class);
        exceptions.put("NoOrderBookingFoundException", NoOrderBookingFoundException.class);
        exceptions.put("NoOrderFoundException", NoOrderFoundException.class);
        exceptions.put("NoPaymentFoundException", NoPaymentFoundException.class);
        exceptions.put("NoProductsInShoppingCartException", NoProductsInShoppingCartException.class);
        exceptions.put("NoSpecifiedProductInWarehouseException", NoSpecifiedProductInWarehouseException.class);
        exceptions.put("NotAuthorizedUserException", NotAuthorizedUserException.class);
        exceptions.put("NotEnoughInfoInOrderToCalculateException", NotEnoughInfoInOrderToCalculateException.class);
        exceptions.put("OrderDeliveryAlreadyExistsException", OrderDeliveryAlreadyExistsException.class);
        exceptions.put("OrderPaymentAlreadyExistsException", OrderPaymentAlreadyExistsException.class);
        exceptions.put("ProductInShoppingCartLowQuantityInWarehouse",
                ProductInShoppingCartLowQuantityInWarehouse.class);
        exceptions.put("ProductInShoppingCartNotInWarehouse", ProductInShoppingCartNotInWarehouse.class);
        exceptions.put("ProductNotFoundException", ProductNotFoundException.class);
        exceptions.put("ShoppingCartDeactivatedException", ShoppingCartDeactivatedException.class);
        exceptions.put("SpecifiedProductAlreadyInWarehouseException",
                SpecifiedProductAlreadyInWarehouseException.class);
        return exceptions;
    }
}
