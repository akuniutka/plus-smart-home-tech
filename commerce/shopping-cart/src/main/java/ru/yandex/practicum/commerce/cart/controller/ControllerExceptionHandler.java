package ru.yandex.practicum.commerce.cart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.exception.ApiException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiException> handleApiException(final ApiException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(exception, exception.getHttpStatus());
    }
}
