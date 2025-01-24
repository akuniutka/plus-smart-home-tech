package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ApiException {

    public ProductNotFoundException(final String userMessage) {
        super(HttpStatus.NOT_FOUND, userMessage);
    }
}
