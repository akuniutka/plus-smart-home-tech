package ru.yandex.practicum.telemetry.analyzer.exception;

import lombok.Getter;

@Getter
public class EntityValidationException extends RuntimeException {

    private final String parameterName;
    private final String parameterValue;

    public EntityValidationException(final String message) {
        this(message, null, null);
    }

    public EntityValidationException(final String message, final String parameterName, final String parameterValue) {
        super(message);
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getAdditionalInfo() {
        return parameterName != null ? ", " + parameterName + " = " + parameterValue : "";
    }
}
