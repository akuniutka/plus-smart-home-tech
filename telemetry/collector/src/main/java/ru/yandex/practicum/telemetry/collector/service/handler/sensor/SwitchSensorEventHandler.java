package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.sender.SensorEventSender;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(final SensorEventSender sender) {
        super(sender);
    }

    @Override
    public SensorEventProto.PayloadCase getPayloadType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapPayload(final SensorEventProto event) {
        final SwitchSensorProto payload = event.getSwitchSensorEvent();
        return SwitchSensorAvro.newBuilder()
                .setState(payload.getState())
                .build();
    }
}
