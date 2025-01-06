package ru.yandex.practicum.telemetry.collector.mapper.condition.type;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.factory.ConditionTypeMapperImpl;

@Component
public class LuminosityConditionTypeMapper implements ConditionTypeMapperImpl.SpecificConditionTypeMapper {

    @Override
    public ConditionTypeProto getConditionTypeProto() {
        return ConditionTypeProto.LUMINOSITY;
    }

    @Override
    public ConditionTypeAvro getConditionTypeAvro() {
        return ConditionTypeAvro.LUMINOSITY;
    }
}
