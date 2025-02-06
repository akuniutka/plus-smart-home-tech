package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class OrderDeliveryAlreadyExistsException extends ApiException {

    public OrderDeliveryAlreadyExistsException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
