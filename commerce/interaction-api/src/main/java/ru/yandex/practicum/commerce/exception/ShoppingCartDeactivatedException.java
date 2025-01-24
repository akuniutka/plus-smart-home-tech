package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class ShoppingCartDeactivatedException extends ApiException {

    public ShoppingCartDeactivatedException(final String userMessage) {
        super(HttpStatus.LOCKED, userMessage);
    }
}
