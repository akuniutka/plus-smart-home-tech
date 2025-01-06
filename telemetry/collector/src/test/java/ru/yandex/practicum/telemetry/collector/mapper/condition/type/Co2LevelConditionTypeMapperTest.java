package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class Co2LevelConditionTypeMapperTest {

    private Co2LevelConditionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Co2LevelConditionTypeMapper();
    }

    @Test
    void whenGetConditionTypeProto_ThenReturnCo2Level() {
        final ConditionTypeProto conditionType = mapper.getConditionTypeProto();

        assertThat(conditionType, equalTo(ConditionTypeProto.CO2LEVEL));
    }

    @Test
    void whenGetConditionTypeAvro_ThenReturnCo2Level() {
        final ConditionTypeAvro conditionType = mapper.getConditionTypeAvro();

        assertThat(conditionType, equalTo(ConditionTypeAvro.CO2LEVEL));
    }
}