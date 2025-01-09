package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class HumidityConditionTypeMapperTest {

    private HumidityConditionTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HumidityConditionTypeMapper();
    }

    @Test
    void whenGetConditionTypeProto_ThenReturnHumidity() {
        final ConditionTypeProto conditionType = mapper.getConditionTypeProto();

        assertThat(conditionType, equalTo(ConditionTypeProto.HUMIDITY));
    }

    @Test
    void whenGetConditionTypeAvro_ThenReturnHumidity() {
        final ConditionTypeAvro conditionType = mapper.getConditionTypeAvro();

        assertThat(conditionType, equalTo(ConditionTypeAvro.HUMIDITY));
    }
}