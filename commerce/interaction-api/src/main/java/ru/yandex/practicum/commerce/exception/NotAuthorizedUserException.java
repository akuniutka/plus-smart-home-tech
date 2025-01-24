package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedUserException extends ApiException {

    public NotAuthorizedUserException(final String userMessage) {
        super(HttpStatus.UNAUTHORIZED, userMessage);
    }
}
