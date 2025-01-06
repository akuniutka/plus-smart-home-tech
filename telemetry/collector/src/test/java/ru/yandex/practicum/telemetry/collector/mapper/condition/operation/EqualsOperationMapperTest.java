package ru.yandex.practicum.telemetry.collector.mapper.condition.operation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class EqualsOperationMapperTest {

    private EqualsOperationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EqualsOperationMapper();
    }

    @Test
    void whenGetOperationProto_ThenReturnEquals() {
        final ConditionOperationProto operation = mapper.getOperationProto();

        assertThat(operation, equalTo(ConditionOperationProto.EQUALS));
    }

    @Test
    void whenGetOperationAvro_ThenReturnEquals() {
        final ConditionOperationAvro operation = mapper.getOperationAvro();

        assertThat(operation, equalTo(ConditionOperationAvro.EQUALS));
    }
}