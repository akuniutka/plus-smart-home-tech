package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class NoSpecifiedProductInWarehouseException extends ApiException {

    public NoSpecifiedProductInWarehouseException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
