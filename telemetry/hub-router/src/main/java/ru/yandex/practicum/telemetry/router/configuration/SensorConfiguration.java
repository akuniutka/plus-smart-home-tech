package ru.yandex.practicum.telemetry.router.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.telemetry.router.device.sensor.AbstractSensor;
import ru.yandex.practicum.telemetry.router.device.sensor.ClimateSensor;
import ru.yandex.practicum.telemetry.router.device.sensor.LightSensor;
import ru.yandex.practicum.telemetry.router.device.sensor.MotionSensor;
import ru.yandex.practicum.telemetry.router.device.sensor.SwitchSensor;
import ru.yandex.practicum.telemetry.router.device.sensor.TemperatureSensor;

import java.util.List;
import java.util.stream.Stream;

@ConfigurationProperties("sensor")
@RequiredArgsConstructor
public class SensorConfiguration {

    private final List<MotionSensor> motionSensors;
    private final List<SwitchSensor> switchSensors;
    private final List<TemperatureSensor> temperatureSensors;
    private final List<LightSensor> lightSensors;
    private final List<ClimateSensor> climateSensors;

    public List<? extends AbstractSensor> getSensors() {
        return Stream.of(motionSensors, switchSensors, temperatureSensors, lightSensors, climateSensors)
                .flatMap(List::stream)
                .toList();
    }
}
