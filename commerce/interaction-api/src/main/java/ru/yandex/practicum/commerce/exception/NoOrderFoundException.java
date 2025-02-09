package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoOrderFoundException extends ApiException {

    public NoOrderFoundException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
