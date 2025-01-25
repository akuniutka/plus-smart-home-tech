package ru.yandex.practicum.commerce.exception;

import org.springframework.http.HttpStatus;

public class SpecifiedProductAlreadyInWarehouseException extends ApiException {

    public SpecifiedProductAlreadyInWarehouseException(final String userMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage);
    }
}
