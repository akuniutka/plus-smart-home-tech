package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.service.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.service.sender.SensorEventSender;

@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final SensorEventSender sender;

    protected abstract T mapToAvro(SensorEvent event);

    public void handle(final SensorEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Unknown sensor event type: " + event.getType());
        }

        final T payload = mapToAvro(event);

        final SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();

        sender.send(eventAvro);
    }
}
