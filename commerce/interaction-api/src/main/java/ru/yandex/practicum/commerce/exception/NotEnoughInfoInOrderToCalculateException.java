package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NotEnoughInfoInOrderToCalculateException extends ApiException {

    public NotEnoughInfoInOrderToCalculateException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
