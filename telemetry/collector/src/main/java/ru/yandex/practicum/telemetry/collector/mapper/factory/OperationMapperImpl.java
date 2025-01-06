package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationMapperImpl implements OperationMapper {

    private final Map<ConditionOperationProto, SpecificOperationMapper> mappers;

    public OperationMapperImpl(final Set<SpecificOperationMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(SpecificOperationMapper::getOperationProto, Function.identity()));
    }

    @Override
    public ConditionOperationAvro mapToAvro(final ConditionOperationProto operation) {
        if (!mappers.containsKey(operation)) {
            throw new IllegalArgumentException("No mapper found for operation " + operation);
        }
        return mappers.get(operation).getOperationAvro();
    }

    public interface SpecificOperationMapper {

        ConditionOperationProto getOperationProto();

        ConditionOperationAvro getOperationAvro();
    }
}
