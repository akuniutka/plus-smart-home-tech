package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;

public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    @Override
    public void handle(final HubEventAvro event) {
        if (!event.getPayload().getClass().getName().equals(getPayloadType())) {
            throw new IllegalArgumentException("Unknown payload type: " + event.getPayload().getClass());
        }
        final String hubId = event.getHubId();
        final T payload = cast(event.getPayload());
        handleInternally(hubId, payload);
    }

    protected abstract T cast(Object payload);

    protected abstract void handleInternally(String hubId, T payload);
}
