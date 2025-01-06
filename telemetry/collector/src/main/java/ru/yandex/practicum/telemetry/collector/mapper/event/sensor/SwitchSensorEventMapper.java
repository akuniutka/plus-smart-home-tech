package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventMapper extends BaseSensorEventMapper<SwitchSensorAvro> {

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
