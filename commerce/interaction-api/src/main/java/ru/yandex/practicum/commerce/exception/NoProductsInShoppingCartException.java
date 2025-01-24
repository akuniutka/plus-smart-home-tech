package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoProductsInShoppingCartException extends ApiException {

    public NoProductsInShoppingCartException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
