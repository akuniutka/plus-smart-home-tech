package ru.yandex.practicum.telemetry.analyzer.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.service.ScenarioService;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    private final ScenarioService service;

    public ScenarioRemovedEventHandler(final ScenarioService service) {
        this.service = service;
    }

    @Override
    public String getPayloadType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    protected ScenarioRemovedEventAvro cast(final Object payload) {
        return (ScenarioRemovedEventAvro) payload;
    }

    @Override
    protected void handleInternally(final String hubId, final ScenarioRemovedEventAvro payload) {
        final Scenario scenario = new Scenario();
        scenario.setName(payload.getName());
        scenario.setHubId(hubId);
        service.delete(scenario);
    }
}
