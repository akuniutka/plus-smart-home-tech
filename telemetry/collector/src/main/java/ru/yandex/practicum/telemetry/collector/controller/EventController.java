package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;

    public EventController(
            final Set<HubEventHandler> hubEventHandlers,
            final Set<SensorEventHandler> sensorEventHandlers
    ) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getPayloadType, Function.identity()));
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getPayloadType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(final SensorEventProto event, final StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Sensor event received: {}", event);
            if (!sensorEventHandlers.containsKey(event.getPayloadCase())) {
                throw new IllegalArgumentException("No handler found for payload type " + event.getPayloadCase());
            }
            sensorEventHandlers.get(event.getPayloadCase()).handle(event);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.debug("Sensor event processed: {}", event);
        } catch (Exception e) {
            log.error("Sensor event processing error: {}, event: {}", e.getMessage(), event, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(final HubEventProto event, final StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Hub event received: {}", event);
            if (!hubEventHandlers.containsKey(event.getPayloadCase())) {
                throw new IllegalArgumentException("No handler found for payload type " + event.getPayloadCase());
            }
            hubEventHandlers.get(event.getPayloadCase()).handle(event);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.debug("Hub event processed: {}", event);
        } catch (Exception e) {
            log.error("Hub event processing error: {}, event: {}", e.getMessage(), event, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
