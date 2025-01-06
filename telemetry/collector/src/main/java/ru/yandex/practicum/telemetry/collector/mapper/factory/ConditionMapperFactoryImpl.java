package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapperFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConditionMapperFactoryImpl implements ConditionMapperFactory {

    private final Map<ScenarioConditionProto.ValueCase, ConditionMapper> mappers;

    public ConditionMapperFactoryImpl(final Set<ConditionMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(ConditionMapper::getValueType, Function.identity()));
    }

    @Override
    public ConditionMapper getMapper(final ScenarioConditionProto.ValueCase valueType) {
        if (!mappers.containsKey(valueType)) {
            throw new IllegalArgumentException("No condition mapper found for value type " + valueType);
        }
        return mappers.get(valueType);
    }
}
