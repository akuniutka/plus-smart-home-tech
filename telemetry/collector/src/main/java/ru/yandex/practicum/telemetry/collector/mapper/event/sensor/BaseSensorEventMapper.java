package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;

import static ru.yandex.practicum.grpc.telemetry.util.Convertors.timestampToInstant;

public abstract class BaseSensorEventMapper<T extends SpecificRecordBase> implements SensorEventMapper {

    public SensorEventAvro mapToAvro(final SensorEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getPayloadType())) {
            throw new IllegalArgumentException("Unknown payload type: " + eventProto.getPayloadCase());
        }

        final T payload = mapPayload(eventProto);

        return SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(timestampToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();
    }

    protected abstract T mapPayload(SensorEventProto event);
}
