package ru.yandex.practicum.telemetry.analyzer.dispatcher.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.dispatcher.HubEventDispatcher;
import ru.yandex.practicum.telemetry.analyzer.exception.EntityValidationException;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandlerFactory;
import ru.yandex.practicum.telemetry.analyzer.util.LogListener;

import static org.mockito.ArgumentMatchers.any;
import static ru.yandex.practicum.telemetry.analyzer.util.TestModels.DEVICE_TYPE;
import static ru.yandex.practicum.telemetry.analyzer.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.analyzer.util.TestModels.SENSOR_ID;
import static ru.yandex.practicum.telemetry.analyzer.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.analyzer.util.TestUtils.assertLogs;

class HubEventDispatcherImplTest {

    private static final LogListener logListener = new LogListener(HubEventDispatcherImpl.class);
    private static final String payloadType = DeviceAddedEventAvro.getClassSchema().getFullName();
    private HubEventHandlerFactory mockHandlerFactory;
    private HubEventHandler mockHandler;
    private HubEventDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        mockHandler = Mockito.mock(HubEventHandler.class);
        mockHandlerFactory = Mockito.mock(HubEventHandlerFactory.class);
        Mockito.when(mockHandlerFactory.getHandler(any())).thenReturn(mockHandler);
        logListener.startListen();
        logListener.reset();
        dispatcher = new HubEventDispatcherImpl(mockHandlerFactory);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockHandler, mockHandlerFactory);
    }

    @Test
    void whenDispatchEvent_ThenGetHandlerAndPassEventToItAndLog() throws Exception {
        dispatcher.dispatch(getTestHubEvent());

        Mockito.verify(mockHandlerFactory).getHandler(payloadType);
        Mockito.verify(mockHandler).handle(getTestHubEvent());
        assertLogs(logListener.getEvents(), "dispatch.json", getClass());
    }

    @Test
    void whenDispatchAndValidationError_ThenLogIt() throws Exception {
        final EntityValidationException exception = new EntityValidationException("Test exception");
        Mockito.doThrow(exception).when(mockHandler).handle(any());

        dispatcher.dispatch(getTestHubEvent());

        Mockito.verify(mockHandlerFactory).getHandler(payloadType);
        Mockito.verify(mockHandler).handle(getTestHubEvent());
        assertLogs(logListener.getEvents(), "dispatch_error.json", getClass());
    }

    @Test
    void whenDispatchAndValidationErrorWithAdditionalInfo_ThenLogIt() throws Exception {
        final EntityValidationException exception = new EntityValidationException("Test exception", "test parameter",
                "test value");
        Mockito.doThrow(exception).when(mockHandler).handle(any());

        dispatcher.dispatch(getTestHubEvent());

        Mockito.verify(mockHandlerFactory).getHandler(payloadType);
        Mockito.verify(mockHandler).handle(getTestHubEvent());
        assertLogs(logListener.getEvents(), "dispatch_error_with_extra_info.json", getClass());
    }

    private HubEventAvro getTestHubEvent() {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(SENSOR_ID)
                        .setType(DEVICE_TYPE)
                        .build())
                .build();
    }
}