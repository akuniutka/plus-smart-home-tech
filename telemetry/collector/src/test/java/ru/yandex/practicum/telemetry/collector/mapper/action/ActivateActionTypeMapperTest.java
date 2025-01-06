package ru.yandex.practicum.telemetry.collector.mapper.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ActivateActionTypeMapperTest {

    private ActivateActionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ActivateActionTypeMapper();
    }

    @Test
    void whenGetActionTypeProto_ThenReturnActivate() {
        final ActionTypeProto actionType = mapper.getActionTypeProto();

        assertThat(actionType, equalTo(ActionTypeProto.ACTIVATE));
    }

    @Test
    void whenGetActionTypeAvro_ThenReturnActivate() {
        final ActionTypeAvro actionType = mapper.getActionTypeAvro();

        assertThat(actionType, equalTo(ActionTypeAvro.ACTIVATE));
    }
}