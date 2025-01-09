package ru.yandex.practicum.telemetry.analyzer.service.impl;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.router.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.telemetry.analyzer.model.DeviceAction;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.service.DeviceActionExecutor;

import java.time.Instant;

import static ru.yandex.practicum.grpc.telemetry.util.Convertors.timestampToInstant;

@Service
public class DeviceActionExecutorImpl implements DeviceActionExecutor {

    private static final Logger log = LoggerFactory.getLogger(DeviceActionExecutorImpl.class);

    @GrpcClient("router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    @Override
    public void execute(final Scenario scenario) {
        scenario.getActions().stream()
                .map(deviceAction -> mapToProtobuf(scenario.getHubId(), scenario.getHubId(), deviceAction))
                .forEach(this::send);
    }

    private DeviceActionRequest mapToProtobuf(final String hubId, final String scenarioName,
            final DeviceAction deviceAction
    ) {
        final Instant timestamp = Instant.now();
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(mapToProtobuf(deviceAction))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }

    private DeviceActionProto mapToProtobuf(final DeviceAction deviceAction) {
        final DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(deviceAction.getDeviceId())
                .setType(mapToProtobuf(deviceAction.getType()));
        if (deviceAction.getValue() != null) {
            builder.setValue(deviceAction.getValue());
        }
        return builder.build();
    }

    private ActionTypeProto mapToProtobuf(final ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

    private void send(final DeviceActionRequest request) {
        hubRouterStub.handleDeviceAction(request);
        log.info("Sent request for device action: hubId = {}, scenarioName = {}, timestamp = {}", request.getHubId(),
                request.getScenarioName(), timestampToInstant(request.getTimestamp()));
        log.debug("Request = {}", request);
    }
}
