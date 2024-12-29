package ru.yandex.practicum.telemetry.analyzer.exception;

public class EntityValidationException extends RuntimeException {

    public EntityValidationException(final String message) {
        super(message);
    }
}
