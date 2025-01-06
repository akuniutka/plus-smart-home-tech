package ru.yandex.practicum.telemetry.collector.mapper.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class InverseActionTypeMapperTest {

    private InverseActionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InverseActionTypeMapper();
    }

    @Test
    void whenGetActionTypeProto_ThenReturnInverse() {
        final ActionTypeProto actionType = mapper.getActionTypeProto();

        assertThat(actionType, equalTo(ActionTypeProto.INVERSE));
    }

    @Test
    void whenGetActionTypeAvro_ThenReturnInverse() {
        final ActionTypeAvro actionType = mapper.getActionTypeAvro();

        assertThat(actionType, equalTo(ActionTypeAvro.INVERSE));
    }
}