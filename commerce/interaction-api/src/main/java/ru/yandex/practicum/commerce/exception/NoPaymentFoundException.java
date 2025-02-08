package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoPaymentFoundException extends ApiException {

    public NoPaymentFoundException(final String userMessage) {
        super(HttpStatus.NOT_FOUND, userMessage);
    }
}
