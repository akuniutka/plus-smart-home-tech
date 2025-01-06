package ru.yandex.practicum.telemetry.collector.mapper.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SensorEventMapperFactoryImpl implements SensorEventMapperFactory {

    private final Map<SensorEventProto.PayloadCase, SensorEventMapper> mappers;

    public SensorEventMapperFactoryImpl(final Set<SensorEventMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(SensorEventMapper::getPayloadType, Function.identity()));
    }

    @Override
    public SensorEventMapper getMapper(final SensorEventProto.PayloadCase payloadType) {
        if (!mappers.containsKey(payloadType)) {
            throw new IllegalArgumentException("No sensor event mapper found for payload type " + payloadType);
        }
        return mappers.get(payloadType);
    }
}
