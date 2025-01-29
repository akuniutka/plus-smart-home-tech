package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class ProductInShoppingCartNotInWarehouse extends ApiException {

    public ProductInShoppingCartNotInWarehouse(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
