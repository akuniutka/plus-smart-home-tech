package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ActionTypeMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ActionTypeMapperImpl implements ActionTypeMapper {

    private final Map<ActionTypeProto, SpecificActionTypeMapper> mappers;

    public ActionTypeMapperImpl(final Set<SpecificActionTypeMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(SpecificActionTypeMapper::getActionTypeProto, Function.identity()));
    }

    @Override
    public ActionTypeAvro mapToAvro(final ActionTypeProto actionType) {
        if (!mappers.containsKey(actionType)) {
            throw new IllegalArgumentException("No mapper found for action type " + actionType);
        }
        return mappers.get(actionType).getActionTypeAvro();
    }

    public interface SpecificActionTypeMapper {

        ActionTypeProto getActionTypeProto();

        ActionTypeAvro getActionTypeAvro();
    }
}
