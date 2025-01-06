package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class LuminosityConditionTypeMapperTest {

    private LuminosityConditionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LuminosityConditionTypeMapper();
    }

    @Test
    void whenGetConditionTypeProto_ThenReturnLuminosity() {
        final ConditionTypeProto conditionType = mapper.getConditionTypeProto();

        assertThat(conditionType, equalTo(ConditionTypeProto.LUMINOSITY));
    }

    @Test
    void whenGetConditionTypeAvro_ThenReturnLuminosity() {
        final ConditionTypeAvro conditionType = mapper.getConditionTypeAvro();

        assertThat(conditionType, equalTo(ConditionTypeAvro.LUMINOSITY));
    }
}