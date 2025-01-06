package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.SCENARIO_NAME;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestHubEventProtoWithNoPayload;

class ScenarioRemovedEventMapperTest {

    private ScenarioRemovedEventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ScenarioRemovedEventMapper();
    }

    @Test
    void whenGetPayloadType_ThenReturnScenarioRemoved() {
        final HubEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(HubEventProto.PayloadCase.SCENARIO_REMOVED));
    }

    @Test
    void whenMapToAvro_ThenReturnCorrectHubEventAvro() {
        final HubEventAvro eventAvro = mapper.mapToAvro(getTestHubEventProto());

        assertThat(eventAvro, equalTo(getTestHubEventAvro()));
    }

    @Test
    void whenMapToAvroAndWrongPayloadType_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(getTestHubEventProtoWithNoPayload()));

        assertThat(e.getMessage(), equalTo("Unknown payload type: PAYLOAD_NOT_SET"));
    }

    private HubEventProto getTestHubEventProto() {
        return HubEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setScenarioRemoved(ScenarioRemovedEventProto.newBuilder()
                        .setName(SCENARIO_NAME)
                        .build())
                .build();
    }

    private HubEventAvro getTestHubEventAvro() {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(SCENARIO_NAME)
                        .build())
                .build();
    }
}