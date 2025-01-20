package ru.yandex.practicum.commerce.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ProductNotFoundException extends RuntimeException {

    private HttpStatus httpStatus;
    private String userMessage;

    public ProductNotFoundException(final String userMessage) {
        super(userMessage);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }
}
