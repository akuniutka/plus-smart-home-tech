package ru.yandex.practicum.telemetry.analyzer.util;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.stream.Stream;

public final class TestModels {

    private static final String HUB_EVENT_PAYLOAD_FIELD_NAME = "payload";

    private TestModels() {
        throw new AssertionError();
    }

    public static Stream<String> getPossibleHubEventPayloadTypes() {
        return HubEventAvro.getClassSchema()
                .getField(HUB_EVENT_PAYLOAD_FIELD_NAME)
                .schema()
                .getTypes()
                .stream()
                .map(Schema::getFullName);
    }
}
