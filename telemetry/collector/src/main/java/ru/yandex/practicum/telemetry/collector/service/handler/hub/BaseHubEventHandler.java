package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.sender.HubEventSender;

import static ru.yandex.practicum.telemetry.collector.util.Convertors.timestampToInstant;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final HubEventSender sender;

    protected BaseHubEventHandler(final HubEventSender sender) {
        this.sender = sender;
    }

    protected abstract T mapPayload(HubEventProto event);

    @Override
    public void handle(final HubEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getPayloadType())) {
            throw new IllegalArgumentException("Unknown payload type: " + eventProto.getPayloadCase());
        }

        final T payload = mapPayload(eventProto);

        final HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(timestampToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();

        sender.send(eventAvro);
    }
}
