package ru.yandex.practicum.telemetry.collector.mapper.condition.operation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class GreaterThanOperationMapperTest {

    private GreaterThanOperationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GreaterThanOperationMapper();
    }

    @Test
    void whenGetOperationProto_ThenReturnGreaterThan() {
        final ConditionOperationProto operation = mapper.getOperationProto();

        assertThat(operation, equalTo(ConditionOperationProto.GREATER_THAN));
    }

    @Test
    void whenGetOperationAvro_ThenReturnGreaterThan() {
        final ConditionOperationAvro operation = mapper.getOperationAvro();

        assertThat(operation, equalTo(ConditionOperationAvro.GREATER_THAN));
    }
}