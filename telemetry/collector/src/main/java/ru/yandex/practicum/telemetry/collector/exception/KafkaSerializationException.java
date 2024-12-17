package ru.yandex.practicum.telemetry.collector.exception;

public class KafkaSerializationException extends RuntimeException {

    public KafkaSerializationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
