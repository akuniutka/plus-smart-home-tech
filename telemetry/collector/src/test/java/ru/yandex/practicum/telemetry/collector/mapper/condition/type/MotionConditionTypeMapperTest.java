package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MotionConditionTypeMapperTest {

    private MotionConditionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MotionConditionTypeMapper();
    }

    @Test
    void whenGetConditionTypeProto_ThenReturnMotion() {
        final ConditionTypeProto conditionType = mapper.getConditionTypeProto();

        assertThat(conditionType, equalTo(ConditionTypeProto.MOTION));
    }

    @Test
    void whenGetConditionTypeAvro_ThenReturnMotion() {
        final ConditionTypeAvro conditionType = mapper.getConditionTypeAvro();

        assertThat(conditionType, equalTo(ConditionTypeAvro.MOTION));
    }
}