package ru.yandex.practicum.commerce.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private HttpStatus httpStatus;
    private String userMessage;

    public ApiException(final HttpStatus httpStatus, final String userMessage) {
        super(userMessage);
        this.httpStatus = httpStatus;
        this.userMessage = userMessage;
    }
}
