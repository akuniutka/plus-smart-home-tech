package ru.yandex.practicum.telemetry.collector.mapper.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class SetValueActionTypeMapperTest {

    private SetValueActionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SetValueActionTypeMapper();
    }

    @Test
    void whenGetActionTypeProto_ThenReturnSetValue() {
        final ActionTypeProto actionType = mapper.getActionTypeProto();

        assertThat(actionType, equalTo(ActionTypeProto.SET_VALUE));
    }

    @Test
    void whenGetActionTypeAvro_ThenReturnSetValue() {
        final ActionTypeAvro actionType = mapper.getActionTypeAvro();

        assertThat(actionType, equalTo(ActionTypeAvro.SET_VALUE));
    }
}