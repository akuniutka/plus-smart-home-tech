package ru.yandex.practicum.telemetry.collector.mapper.condition.operation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LowerThanOperationMapperTest {

    private LowerThanOperationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LowerThanOperationMapper();
    }

    @Test
    void whenGetOperationProto_ThenReturnLowerThan() {
        final ConditionOperationProto operation = mapper.getOperationProto();

        assertThat(operation, equalTo(ConditionOperationProto.LOWER_THAN));
    }

    @Test
    void whenGetOperationAvro_ThenReturnLowerThan() {
        final ConditionOperationAvro operation = mapper.getOperationAvro();

        assertThat(operation, equalTo(ConditionOperationAvro.LOWER_THAN));
    }
}