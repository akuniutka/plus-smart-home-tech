package ru.yandex.practicum.telemetry.analyzer.util;

import org.apache.avro.Schema;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;
import java.util.stream.Stream;

public final class TestModels {

    public static final String HUB_ID = "test.hub.1";
    public static final String SENSOR_ID = "test.temperature.sensor.1";
    public static final Instant TIMESTAMP = Instant.parse("2000-01-31T13:30:55.123Z");
    public static final DeviceTypeAvro DEVICE_TYPE = DeviceTypeAvro.TEMPERATURE_SENSOR;

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
