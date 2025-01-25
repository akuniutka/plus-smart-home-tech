package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class ProductInShoppingCartLowQuantityInWarehouse extends ApiException {

    public ProductInShoppingCartLowQuantityInWarehouse(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
