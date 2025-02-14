package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoOrderBookingFoundException extends ApiException {

    public NoOrderBookingFoundException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
