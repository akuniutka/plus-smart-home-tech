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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;

import static ru.yandex.practicum.grpc.telemetry.util.Convertors.timestampToInstant;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final HubEventService hubEventService;
    private final SensorEventService sensorEventService;
    private final HubEventMapperFactory hubEventMapperFactory;
    private final SensorEventMapperFactory sensorEventMapperFactory;

    public EventController(
            final HubEventService hubEventService,
            final SensorEventService sensorEventService,
            final HubEventMapperFactory hubEventMapperFactory,
            final SensorEventMapperFactory sensorEventMapperFactory
    ) {
        this.hubEventService = hubEventService;
        this.sensorEventService = sensorEventService;
        this.hubEventMapperFactory = hubEventMapperFactory;
        this.sensorEventMapperFactory = sensorEventMapperFactory;
    }

    @Override
    public void collectHubEvent(final HubEventProto eventProto, final StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received hub event: hubId = {}, timestamp = {}, payload type = {}",
                    eventProto.getHubId(), timestampToInstant(eventProto.getTimestamp()), eventProto.getPayloadCase());
            log.debug("Hub event = {}", eventProto);
            final HubEventMapper mapper = hubEventMapperFactory.getMapper(eventProto.getPayloadCase());
            final HubEventAvro eventAvro = mapper.mapToAvro(eventProto);
            hubEventService.addToProcessingQueue(eventAvro);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Hub event processing error: {}, event: {}", e.getMessage(), eventProto, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectSensorEvent(final SensorEventProto eventProto, final StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received sensor event: hubId = {}, sensorId = {}, timestamp = {}",
                    eventProto.getHubId(), eventProto.getId(), timestampToInstant(eventProto.getTimestamp()));
            log.debug("Sensor event = {}", eventProto);
            final SensorEventMapper mapper = sensorEventMapperFactory.getMapper(eventProto.getPayloadCase());
            final SensorEventAvro eventAvro = mapper.mapToAvro(eventProto);
            sensorEventService.addToProcessingQueue(eventAvro);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Sensor event processing error: {}, event: {}", e.getMessage(), eventProto, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
