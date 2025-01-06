package ru.yandex.practicum.telemetry.collector.mapper.event.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorEventMapper extends BaseSensorEventMapper<ClimateSensorAvro> {

    @Override
    public SensorEventProto.PayloadCase getPayloadType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    protected ClimateSensorAvro mapPayload(final SensorEventProto event) {
        final ClimateSensorProto payload = event.getClimateSensorEvent();
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(payload.getTemperatureC())
                .setHumidity(payload.getHumidity())
                .setCo2Level(payload.getCo2Level())
                .build();
    }
}
