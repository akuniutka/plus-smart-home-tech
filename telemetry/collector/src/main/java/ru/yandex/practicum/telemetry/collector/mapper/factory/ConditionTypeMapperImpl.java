package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConditionTypeMapperImpl implements ConditionTypeMapper {

    private final Map<ConditionTypeProto, SpecificConditionTypeMapper> mappers;

    public ConditionTypeMapperImpl(final Set<SpecificConditionTypeMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(SpecificConditionTypeMapper::getConditionTypeProto, Function.identity()));
    }

    @Override
    public ConditionTypeAvro mapToAvro(final ConditionTypeProto conditionType) {
        if (!mappers.containsKey(conditionType)) {
            throw new IllegalArgumentException("No mapper found for condition type " + conditionType);
        }
        return mappers.get(conditionType).getConditionTypeAvro();
    }

    public interface SpecificConditionTypeMapper {

        ConditionTypeProto getConditionTypeProto();

        ConditionTypeAvro getConditionTypeAvro();
    }
}
