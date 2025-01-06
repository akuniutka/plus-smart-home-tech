package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;
import ru.yandex.practicum.telemetry.collector.util.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestUtils.assertLogs;

class EventControllerTest {

    private static final LogListener logListener = new LogListener(EventController.class);
    private static final String SENSOR_ID = "test.light.sensor.1";
    public static final int LINK_QUALITY = 95;
    public static final int LUMINOSITY = 60;
    private static final String EXCEPTION_MESSAGE = "Test exception";
    private AutoCloseable openMocks;
    private InOrder inOrder;

    @Mock
    private HubEventService mockHubEventService;

    @Mock
    private SensorEventService mockSensorEventService;

    @Mock
    private HubEventMapperFactory mockHubEventMapperFactory;

    @Mock
    private SensorEventMapperFactory mockSensorEventMapperFactory;

    @Mock
    private HubEventMapper mockHubEventMapper;

    @Mock
    private SensorEventMapper mockSensorEventMapper;

    @Mock
    private StreamObserver<Empty> mockStreamObserver;

    @Captor
    private ArgumentCaptor<StatusRuntimeException> exceptionCaptor;

    private RuntimeException testException;

    private EventController controller;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        testException = new RuntimeException(EXCEPTION_MESSAGE);
        inOrder = Mockito.inOrder(mockHubEventService, mockSensorEventService, mockHubEventMapperFactory,
                mockSensorEventMapperFactory, mockHubEventMapper, mockSensorEventMapper, mockStreamObserver);
        logListener.startListen();
        logListener.reset();
        controller = new EventController(mockHubEventService, mockSensorEventService, mockHubEventMapperFactory,
                mockSensorEventMapperFactory);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockHubEventService, mockSensorEventService, mockHubEventMapperFactory,
                mockSensorEventMapperFactory, mockHubEventMapper, mockSensorEventMapper, mockStreamObserver);
        openMocks.close();
    }

    @Test
    void whenCollectHubEvent_ThenGetMapperAndMapToAvroAndPassToServiceAndLog() throws Exception {
        when(mockHubEventMapperFactory.getMapper(any())).thenReturn(mockHubEventMapper);
        when(mockHubEventMapper.mapToAvro(any())).thenReturn(getTestHubEventAvro());

        controller.collectHubEvent(getTestHubEventProto(), mockStreamObserver);

        inOrder.verify(mockHubEventMapperFactory).getMapper(HubEventProto.PayloadCase.DEVICE_REMOVED);
        inOrder.verify(mockHubEventMapper).mapToAvro(getTestHubEventProto());
        inOrder.verify(mockHubEventService).addToProcessingQueue(getTestHubEventAvro());
        inOrder.verify(mockStreamObserver).onNext(Empty.getDefaultInstance());
        inOrder.verify(mockStreamObserver).onCompleted();
        assertLogs(logListener.getEvents(), "collect_hub_event.json", getClass());
    }

    @Test
    void whenCollectHubEventAndExceptionInMapperFactory_ThenLogItAndCallOnError() throws Exception {
        when(mockHubEventMapperFactory.getMapper(any())).thenThrow(testException);

        controller.collectHubEvent(getTestHubEventProto(), mockStreamObserver);

        inOrder.verify(mockHubEventMapperFactory).getMapper(HubEventProto.PayloadCase.DEVICE_REMOVED);
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_hub_event_error.json", getClass());
    }

    @Test
    void whenCollectHubEventAndExceptionInMapper_ThenLogItAndCallOnError() throws Exception {
        when(mockHubEventMapperFactory.getMapper(any())).thenReturn(mockHubEventMapper);
        when(mockHubEventMapper.mapToAvro(any())).thenThrow(testException);

        controller.collectHubEvent(getTestHubEventProto(), mockStreamObserver);

        inOrder.verify(mockHubEventMapperFactory).getMapper(HubEventProto.PayloadCase.DEVICE_REMOVED);
        inOrder.verify(mockHubEventMapper).mapToAvro(getTestHubEventProto());
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_hub_event_error.json", getClass());
    }

    @Test
    void whenCollectHubEventAndExceptionInService_ThenLogItAndCallOnError() throws Exception {
        when(mockHubEventMapperFactory.getMapper(any())).thenReturn(mockHubEventMapper);
        when(mockHubEventMapper.mapToAvro(any())).thenReturn(getTestHubEventAvro());
        doThrow(testException).when(mockHubEventService).addToProcessingQueue(any());

        controller.collectHubEvent(getTestHubEventProto(), mockStreamObserver);

        inOrder.verify(mockHubEventMapperFactory).getMapper(HubEventProto.PayloadCase.DEVICE_REMOVED);
        inOrder.verify(mockHubEventMapper).mapToAvro(getTestHubEventProto());
        inOrder.verify(mockHubEventService).addToProcessingQueue(getTestHubEventAvro());
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_hub_event_error.json", getClass());
    }

    @Test
    void whenCollectSensorEvent_ThenGetMapperAndMapToAvroAndPassToServiceAndLog() throws Exception {
        when(mockSensorEventMapperFactory.getMapper(any())).thenReturn(mockSensorEventMapper);
        when(mockSensorEventMapper.mapToAvro(any())).thenReturn(getTestSensorEventAvro());

        controller.collectSensorEvent(getTestSensorEventProto(), mockStreamObserver);

        inOrder.verify(mockSensorEventMapperFactory).getMapper(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT);
        inOrder.verify(mockSensorEventMapper).mapToAvro(getTestSensorEventProto());
        inOrder.verify(mockSensorEventService).addToProcessingQueue(getTestSensorEventAvro());
        inOrder.verify(mockStreamObserver).onNext(Empty.getDefaultInstance());
        inOrder.verify(mockStreamObserver).onCompleted();
        assertLogs(logListener.getEvents(), "collect_sensor_event.json", getClass());
    }

    @Test
    void whenCollectSensorEventAndExceptionInMapperFactory_ThenLogItAndCallOnerror() throws Exception {
        when(mockSensorEventMapperFactory.getMapper(any())).thenThrow(testException);

        controller.collectSensorEvent(getTestSensorEventProto(), mockStreamObserver);

        inOrder.verify(mockSensorEventMapperFactory).getMapper(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT);
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_sensor_event_error.json", getClass());
    }

    @Test
    void whenCollectSensorEventAndExceptionINMapper_ThenLogItAndCallOnError() throws Exception {
        when(mockSensorEventMapperFactory.getMapper(any())).thenReturn(mockSensorEventMapper);
        when(mockSensorEventMapper.mapToAvro(any())).thenThrow(testException);

        controller.collectSensorEvent(getTestSensorEventProto(), mockStreamObserver);

        inOrder.verify(mockSensorEventMapperFactory).getMapper(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT);
        inOrder.verify(mockSensorEventMapper).mapToAvro(getTestSensorEventProto());
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_sensor_event_error.json", getClass());
    }

    @Test
    void whenCollectSensorEventAndExceptionInService_ThenLogItAndCallOnError() throws Exception {
        when(mockSensorEventMapperFactory.getMapper(any())).thenReturn(mockSensorEventMapper);
        when(mockSensorEventMapper.mapToAvro(any())).thenReturn(getTestSensorEventAvro());
        doThrow(testException).when(mockSensorEventService).addToProcessingQueue(any());

        controller.collectSensorEvent(getTestSensorEventProto(), mockStreamObserver);

        inOrder.verify(mockSensorEventMapperFactory).getMapper(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT);
        inOrder.verify(mockSensorEventMapper).mapToAvro(getTestSensorEventProto());
        inOrder.verify(mockSensorEventService).addToProcessingQueue(getTestSensorEventAvro());
        inOrder.verify(mockStreamObserver).onError(exceptionCaptor.capture());
        final StatusRuntimeException exception = exceptionCaptor.getValue();
        assertException(exception);
        assertLogs(logListener.getEvents(), "collect_sensor_event_error.json", getClass());
    }

    private HubEventProto getTestHubEventProto() {
        return HubEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setDeviceRemoved(DeviceRemovedEventProto.newBuilder()
                        .setId(SENSOR_ID)
                        .build())
                .build();
    }

    private SensorEventProto getTestSensorEventProto() {
        return SensorEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setLightSensorEvent(LightSensorProto.newBuilder()
                        .setLinkQuality(LINK_QUALITY)
                        .setLuminosity(LUMINOSITY)
                        .build())
                .build();
    }

    private HubEventAvro getTestHubEventAvro() {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(SENSOR_ID)
                        .build())
                .build();
    }

    private SensorEventAvro getTestSensorEventAvro() {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(LINK_QUALITY)
                        .setLuminosity(LUMINOSITY)
                        .build())
                .build();
    }

    private void assertException(final StatusRuntimeException exception) {
        final Status status = exception.getStatus();
        assertThat(status.getCode(), equalTo(Status.Code.INTERNAL));
        assertThat(status.getDescription(), equalTo(EXCEPTION_MESSAGE));
        assertThat(status.getCause(), sameInstance(testException));
    }
}