package ru.yandex.practicum.telemetry.router.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.router.HubRouterControllerGrpc;

import static ru.yandex.practicum.grpc.telemetry.util.Convertors.timestampToInstant;

@GrpcService
public class DeviceActionController extends HubRouterControllerGrpc.HubRouterControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(DeviceActionController.class);

    @Override
    public void handleDeviceAction(final DeviceActionRequest request, final StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received request for device action: hubId = {}, scenarioName = {}, timestamp = {}",
                    request.getHubId(), request.getScenarioName(), timestampToInstant(request.getTimestamp()));
            log.debug("Request = {}", request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Request processing error: {}, request: {}", e.getMessage(), request, e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
