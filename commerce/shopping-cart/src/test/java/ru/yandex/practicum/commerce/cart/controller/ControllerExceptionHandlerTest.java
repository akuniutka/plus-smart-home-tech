package ru.yandex.practicum.commerce.cart.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.cart.util.LogListener;
import ru.yandex.practicum.commerce.exception.ApiException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static ru.yandex.practicum.commerce.cart.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.cart.util.TestUtils.assertLogs;

class ControllerExceptionHandlerTest {

    private static final LogListener loglistener = new LogListener(ControllerExceptionHandler.class);
    private ApiException mockException;

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        mockException = Mockito.mock(ApiException.class);
        loglistener.startListen();
        loglistener.reset();
        handler = new ControllerExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        loglistener.stopListen();
        Mockito.verifyNoMoreInteractions(mockException);
    }

    @Test
    void whenHandleApiException_ThanWrapItInResponseEntityAndReturnAndLof() throws Exception {
        Mockito.when(mockException.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        Mockito.when(mockException.getMessage()).thenReturn(TEST_EXCEPTION_MESSAGE);

        final ResponseEntity<ApiException> response = handler.handleApiException(mockException);

        Mockito.verify(mockException).getHttpStatus();
        Mockito.verify(mockException).getMessage();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), sameInstance(mockException));
        assertLogs(loglistener.getEvents(), "api_exception.json", getClass());
    }
}