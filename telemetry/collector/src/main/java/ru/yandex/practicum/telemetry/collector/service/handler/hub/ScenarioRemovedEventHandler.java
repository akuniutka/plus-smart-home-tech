package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.sender.HubEventSender;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(final HubEventSender sender) {
        super(sender);
    }

    @Override
    public HubEventProto.PayloadCase getPayloadType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    protected ScenarioRemovedEventAvro mapPayload(final HubEventProto event) {
        final ScenarioRemovedEventProto payload = event.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(payload.getName())
                .build();
    }
}
