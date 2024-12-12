package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.dto.sensor.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.service.sender.SensorEventSender;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(final SensorEventSender sender) {
        super(sender);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(final SensorEvent event) {
        final SwitchSensorEvent _event = (SwitchSensorEvent) event;
        return SwitchSensorAvro.newBuilder()
                .setState(_event.getState())
                .build();
    }
}
