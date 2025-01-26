package ru.yandex.practicum.commerce.cart.exception;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.commerce.exception.ApiException;

public class BookingNotPossibleException extends ApiException {

    public BookingNotPossibleException(final String userMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, userMessage);
    }
}
