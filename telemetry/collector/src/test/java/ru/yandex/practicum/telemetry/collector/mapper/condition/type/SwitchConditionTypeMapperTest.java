package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class SwitchConditionTypeMapperTest {

    private SwitchConditionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SwitchConditionTypeMapper();
    }

    @Test
    void whenGetConditionTypeProto_ThenReturnSwitch() {
        final ConditionTypeProto conditionType = mapper.getConditionTypeProto();

        assertThat(conditionType, equalTo(ConditionTypeProto.SWITCH));
    }

    @Test
    void whenGetConditionTypeAvro_ThenReturnSwitch() {
        final ConditionTypeAvro conditionType = mapper.getConditionTypeAvro();

        assertThat(conditionType, equalTo(ConditionTypeAvro.SWITCH));
    }
}