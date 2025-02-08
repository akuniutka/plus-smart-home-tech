package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class OrderPaymentAlreadyExistsException extends ApiException {

    public OrderPaymentAlreadyExistsException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
