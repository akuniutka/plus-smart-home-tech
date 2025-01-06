package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventMapper extends BaseSensorEventMapper<TemperatureSensorAvro> {

    @Override
    public SensorEventProto.PayloadCase getPayloadType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    protected TemperatureSensorAvro mapPayload(final SensorEventProto event) {
        final TemperatureSensorProto payload = event.getTemperatureSensorEvent();
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(payload.getTemperatureC())
                .setTemperatureF(payload.getTemperatureF())
                .build();
    }
}
