package ru.yandex.practicum.telemetry.router.device.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;

import java.util.UUID;

public class ScenarioRemover extends AbstractHubDevice {

    @Override
    public void addPayload(final HubEventProto.Builder builder) {
        final String name = UUID.randomUUID().toString();
        builder.setScenarioRemoved(ScenarioRemovedEventProto.newBuilder()
                .setName(name)
                .build());
    }
}
