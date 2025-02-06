package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoDeliveryFoundException extends ApiException {

    public NoDeliveryFoundException(final String userMessage) {
        super(HttpStatus.NOT_FOUND, userMessage);
    }
}
