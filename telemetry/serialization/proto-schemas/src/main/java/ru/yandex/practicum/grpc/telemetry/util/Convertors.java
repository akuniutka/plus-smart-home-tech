package ru.yandex.practicum.grpc.telemetry.util;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public final class Convertors {

    private Convertors() {
    }

    public static Instant timestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
