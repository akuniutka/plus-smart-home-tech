package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.service.sender.HubEventSender;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final HubEventSender sender;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(final HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown hub event type: " + event.getType());
        }

        final T payload = mapToAvro(event);

        final HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        sender.send(eventAvro);
    }
}
