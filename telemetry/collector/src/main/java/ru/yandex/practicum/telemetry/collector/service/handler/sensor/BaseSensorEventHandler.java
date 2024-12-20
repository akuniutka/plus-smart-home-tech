package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.telemetry.collector.service.sender.SensorEventSender;

import static ru.yandex.practicum.telemetry.collector.util.Convertors.timestampToInstant;

public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {

    protected final SensorEventSender sender;

    protected BaseSensorEventHandler(final SensorEventSender sender) {
        this.sender = sender;
    }

    protected abstract T mapPayload(SensorEventProto event);

    public void handle(final SensorEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getPayloadType())) {
            throw new IllegalArgumentException("Unknown payload type: " + eventProto.getPayloadCase());
        }

        final T payload = mapPayload(eventProto);

        final SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(timestampToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();

        sender.send(eventAvro);
    }
}
