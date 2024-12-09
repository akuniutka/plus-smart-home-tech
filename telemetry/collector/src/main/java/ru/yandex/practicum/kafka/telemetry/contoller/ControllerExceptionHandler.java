package ru.yandex.practicum.kafka.telemetry.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.kafka.telemetry.dto.error.ApiError;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerExceptionHandler {

    private final Clock clock;

    @ExceptionHandler
    public ResponseEntity<Object> handleWrongValuesInRequestBody(final MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage());
        final List<FieldErrorData> errors = extractValidationErrors(exception);
        final Set<String> fields = errors.stream()
                .map(FieldErrorData::field)
                .map("'%s'"::formatted)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason(composeErrorMessage(fields, errors))
                .errors(errors)
                .timestamp(Instant.now(clock))
                .build();
        log.debug("Responded {} to request: {}", apiError.status(), apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        final ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason(exception.getMessage())
                .timestamp(Instant.now(clock))
                .build();
        log.debug("Responded {} to request: {}", apiError.status(), apiError);
        return new ResponseEntity<>(apiError, apiError.status());
    }

    protected List<FieldErrorData> extractValidationErrors(final MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(error -> new FieldErrorData(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .sorted(Comparator.comparing(FieldErrorData::field).thenComparing(FieldErrorData::error))
                .toList();
    }

    protected String composeErrorMessage(final Set<String> fields, final List<FieldErrorData> errors) {
        if (fields.size() == 1 && errors.size() == 1) {
            return "There is an error in field %s".formatted(fields.iterator().next());
        } else if (fields.size() == 1) {
            return "There are errors in field %s".formatted(fields.iterator().next());
        } else {
            return "There are errors in fields %s".formatted(String.join(", ", fields));
        }
    }

    protected record FieldErrorData(String field, String error, Object value) {

    }
}
