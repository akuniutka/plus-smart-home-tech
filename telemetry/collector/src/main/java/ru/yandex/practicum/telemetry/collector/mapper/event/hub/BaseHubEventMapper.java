package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;

import static ru.yandex.practicum.grpc.telemetry.util.Convertors.timestampToInstant;

public abstract class BaseHubEventMapper<T extends SpecificRecordBase> implements HubEventMapper {

    @Override
    public HubEventAvro mapToAvro(final HubEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getPayloadType())) {
            throw new IllegalArgumentException("Unknown payload type: " + eventProto.getPayloadCase());
        }

        final T payload = mapPayload(eventProto);

        return HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(timestampToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();
    }

    protected abstract T mapPayload(HubEventProto event);
}
