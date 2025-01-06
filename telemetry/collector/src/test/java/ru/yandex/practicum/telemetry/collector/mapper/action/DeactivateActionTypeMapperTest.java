package ru.yandex.practicum.telemetry.collector.mapper.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class DeactivateActionTypeMapperTest {

    private DeactivateActionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DeactivateActionTypeMapper();
    }

    @Test
    void whenGetActionTypeProto_ThenReturnDeactivate() {
        final ActionTypeProto actionType = mapper.getActionTypeProto();

        assertThat(actionType, equalTo(ActionTypeProto.DEACTIVATE));
    }

    @Test
    void whenGetActionTypeAvro_ThenReturnDeactivate() {
        final ActionTypeAvro actionType = mapper.getActionTypeAvro();

        assertThat(actionType, equalTo(ActionTypeAvro.DEACTIVATE));
    }
}