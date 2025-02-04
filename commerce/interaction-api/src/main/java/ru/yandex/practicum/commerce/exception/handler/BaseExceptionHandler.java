package ru.yandex.practicum.commerce.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.commerce.exception.ApiException;
import ru.yandex.practicum.commerce.exception.ApiExceptions;

public class BaseExceptionHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler
    public ResponseEntity<ApiException> handleApiException(final ApiException exception) {
        log.warn(exception.getMessage());
        final HttpHeaders headers = new HttpHeaders();
        headers.add(ApiExceptions.API_EXCEPTION_HEADER, exception.getClass().getSimpleName());
        return new ResponseEntity<>(exception, headers, exception.getHttpStatus());
    }
}
