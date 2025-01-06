package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventMapper extends BaseSensorEventMapper<MotionSensorAvro> {

    @Override
    public SensorEventProto.PayloadCase getPayloadType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    protected MotionSensorAvro mapPayload(final SensorEventProto event) {
        final MotionSensorProto payload = event.getMotionSensorEvent();
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(payload.getLinkQuality())
                .setMotion(payload.getMotion())
                .setVoltage(payload.getVoltage())
                .build();
    }
}
