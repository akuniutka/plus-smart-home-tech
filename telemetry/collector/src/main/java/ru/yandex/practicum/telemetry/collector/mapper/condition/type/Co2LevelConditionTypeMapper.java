package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.factory.ConditionTypeMapperImpl;

@Component
public class Co2LevelConditionTypeMapper implements ConditionTypeMapperImpl.SpecificConditionTypeMapper {

    @Override
    public ConditionTypeProto getConditionTypeProto() {
        return ConditionTypeProto.CO2LEVEL;
    }

    @Override
    public ConditionTypeAvro getConditionTypeAvro() {
        return ConditionTypeAvro.CO2LEVEL;
    }
}
