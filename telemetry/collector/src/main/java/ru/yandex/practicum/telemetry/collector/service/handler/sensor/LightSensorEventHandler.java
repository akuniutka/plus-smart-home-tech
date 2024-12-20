package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.sender.SensorEventSender;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(final SensorEventSender sender) {
        super(sender);
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected LightSensorAvro mapPayload(final SensorEventProto event) {
        final LightSensorProto payload = event.getLightSensorEvent();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(payload.getLinkQuality())
                .setLuminosity(payload.getLuminosity())
                .build();
    }
}
