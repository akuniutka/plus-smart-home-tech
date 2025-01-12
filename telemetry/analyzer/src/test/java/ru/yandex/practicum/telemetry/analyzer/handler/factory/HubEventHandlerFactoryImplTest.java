package ru.yandex.practicum.telemetry.analyzer.handler.factory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandlerFactory;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.telemetry.analyzer.util.TestModels.getPossibleHubEventPayloadTypes;

class HubEventHandlerFactoryImplTest {

    private static final Random random = new Random();
    private String payloadTypeToCheck;
    private Set<HubEventHandler> mockHandlers;

    private HubEventHandlerFactory hubEventHandlerFactory;

    @BeforeAll
    static void init() {
        if (getPossibleHubEventPayloadTypes().findAny().isEmpty()) {
            throw new AssertionError("List of payload types cannot be empty");
        }
    }

    @BeforeEach
    void setUp() {
        final List<String> payloadTypes = getPossibleHubEventPayloadTypes().toList();
        final int randomIdx = random.nextInt(payloadTypes.size());
        payloadTypeToCheck = payloadTypes.get(randomIdx);
        mockHandlers = getPossibleHubEventPayloadTypes()
                .filter(payloadType -> !payloadTypeToCheck.equals(payloadType))
                .map(this::mockHandler)
                .collect(Collectors.toSet());
    }

    @AfterEach
    void tearDown() {
        mockHandlers.forEach(mockHandler -> {
            Mockito.verify(mockHandler).getPayloadType();
            Mockito.verifyNoMoreInteractions(mockHandler);
        });
    }

    @Test
    void whenGetHandlerAndHandlerExist_ThenReturnCorrectHandler() {
        final HubEventHandler expectedHandler = mockHandler(payloadTypeToCheck);
        mockHandlers.add(expectedHandler);
        hubEventHandlerFactory = new HubEventHandlerFactoryImpl(mockHandlers);

        final HubEventHandler actualHandler = hubEventHandlerFactory.getHandler(payloadTypeToCheck);

        assertThat(actualHandler, sameInstance(expectedHandler));
    }

    @Test
    void whenGetHandlerAndHandlerNotExist_ThenThrowException() {
        hubEventHandlerFactory = new HubEventHandlerFactoryImpl(mockHandlers);

        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> hubEventHandlerFactory.getHandler(payloadTypeToCheck));

        assertThat(e.getMessage(), equalTo("No hub event handler found for payload type " + payloadTypeToCheck));
    }

    private HubEventHandler mockHandler(final String payloadType) {
        final HubEventHandler handler = Mockito.mock(HubEventHandler.class);
        Mockito.when(handler.getPayloadType()).thenReturn(payloadType);
        return handler;
    }
}