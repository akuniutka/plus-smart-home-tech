package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HubEventMapperFactoryImpl implements HubEventMapperFactory {

    private final Map<HubEventProto.PayloadCase, HubEventMapper> mappers;

    public HubEventMapperFactoryImpl(final Set<HubEventMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(HubEventMapper::getPayloadType, Function.identity()));
    }

    @Override
    public HubEventMapper getMapper(final HubEventProto.PayloadCase payloadType) {
        if (!mappers.containsKey(payloadType)) {
            throw new IllegalArgumentException("No hub event mapper found for payload type " + payloadType);
        }
        return mappers.get(payloadType);
    }
}
