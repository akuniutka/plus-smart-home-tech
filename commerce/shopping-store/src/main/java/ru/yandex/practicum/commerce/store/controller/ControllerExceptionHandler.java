package ru.yandex.practicum.commerce.store.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ProductNotFoundException> handleProductNotFoundException(
            final ProductNotFoundException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(exception, exception.getHttpStatus());
    }
}
