package ru.yandex.practicum.telemetry.router.device.hub;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.telemetry.router.device.sensor.AbstractSensor;

import java.util.List;
import java.util.UUID;

public class ScenarioAdder extends AbstractHubDevice {

    private final List<? extends AbstractSensor> sensors;

    public ScenarioAdder(final List<? extends AbstractSensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public void addPayload(final HubEventProto.Builder builder) {
        final int sensorIndex = random.nextInt(0, sensors.size());
        final AbstractSensor sensor = sensors.get(sensorIndex);
        final String name = UUID.randomUUID().toString();
        final ScenarioConditionProto condition = ScenarioConditionProto.newBuilder()
                .setSensorId(sensor.getId())
                .setType(ConditionTypeProto.SWITCH)
                .setOperation(ConditionOperationProto.EQUALS)
                .setBoolValue(true)
                .build();
        final DeviceActionProto action = DeviceActionProto.newBuilder()
                .setSensorId(sensor.getId())
                .setType(ActionTypeProto.INVERSE)
                .build();
        builder.setScenarioAdded(ScenarioAddedEventProto.newBuilder()
                        .setName(name)
                        .addCondition(condition)
                        .addAction(action)
                .build());
    }
}
