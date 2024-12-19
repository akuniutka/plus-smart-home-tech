package ru.yandex.practicum.telemetry.router.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.router.configuration.SensorConfiguration;
import ru.yandex.practicum.telemetry.router.device.hub.AbstractHubDevice;
import ru.yandex.practicum.telemetry.router.device.hub.DeviceAdder;
import ru.yandex.practicum.telemetry.router.device.hub.DeviceRemover;
import ru.yandex.practicum.telemetry.router.device.hub.ScenarioAdder;
import ru.yandex.practicum.telemetry.router.device.hub.ScenarioRemover;
import ru.yandex.practicum.telemetry.router.device.sensor.AbstractSensor;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Component
@Slf4j
public class EventDataProducer {

    private static final Random random = new Random();
    private final String hubId;
    private final List<? extends AbstractSensor> sensors;
    private final List<AbstractHubDevice> hubDevices;

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    public EventDataProducer(@Value("${hubId}") final String hubId, final SensorConfiguration sensorConfiguration) {
        this.hubId = hubId;
        this.sensors = sensorConfiguration.getSensors();
        this.hubDevices = List.of(
                new DeviceAdder(sensors),
                new DeviceRemover(sensors),
                new ScenarioAdder(sensors),
                new ScenarioRemover()
        );
    }

    @Scheduled(fixedDelay = 1_000)
    public void generateEvent() {
        if (random.nextBoolean()) {
            generateHubEvent();
        } else {
            generateSensorEvent();
        }
    }

    public void generateHubEvent() {
        final int hubDeviceIndex = random.nextInt(hubDevices.size());
        final AbstractHubDevice hubDevice = hubDevices.get(hubDeviceIndex);
        send(hubDevice::addPayload);
    }

    public void generateSensorEvent() {
        final int sensorIndex = random.nextInt(sensors.size());
        final AbstractSensor sensor = sensors.get(sensorIndex);
        send(sensor.getId(), sensor::addPayload);
    }


    public void send(final Consumer<HubEventProto.Builder> payloadAdder) {
        final HubEventProto.Builder builder = HubEventProto.newBuilder();
        payloadAdder.accept(builder);
        final Instant timestamp = Instant.now();
        final HubEventProto event = builder
                .setHubId(hubId)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
        log.debug("Sending hub event: {}", event);
        collectorStub.collectHubEvent(event);
    }

    public void send(final String sensorId, final Consumer<SensorEventProto.Builder> payloadAdder) {
        final SensorEventProto.Builder builder = SensorEventProto.newBuilder();
        payloadAdder.accept(builder);
        final Instant timestamp = Instant.now();
        final SensorEventProto event = builder
                .setId(sensorId)
                .setHubId(hubId)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
        log.debug("Sending sensor event: {}", event);
        collectorStub.collectSensorEvent(event);
    }
}
