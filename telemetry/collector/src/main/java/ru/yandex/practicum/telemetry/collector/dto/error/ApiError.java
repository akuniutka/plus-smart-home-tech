package ru.yandex.practicum.telemetry.collector.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record ApiError(HttpStatus status, String reason, Object errors, Instant timestamp) {

}