package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventMapper extends BaseHubEventMapper<ScenarioRemovedEventAvro> {

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
