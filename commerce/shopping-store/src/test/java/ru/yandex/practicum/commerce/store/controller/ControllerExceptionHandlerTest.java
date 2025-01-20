package ru.yandex.practicum.commerce.store.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.util.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static ru.yandex.practicum.commerce.store.util.TestModels.EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.store.util.TestUtils.assertLogs;

class ControllerExceptionHandlerTest {

    private static final LogListener logListener = new LogListener(ControllerExceptionHandler.class);
    private ProductNotFoundException mockException;

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        mockException = Mockito.mock(ProductNotFoundException.class);
        logListener.startListen();
        logListener.reset();
        handler = new ControllerExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockException);
    }

    @Test
    void whenHandleProductNotFoundException_ThenWrapItInResponseEntityAndReturnAndLog() throws Exception {
        Mockito.when(mockException.getHttpStatus()).thenReturn(HttpStatus.NOT_FOUND);
        Mockito.when(mockException.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        final ResponseEntity<ProductNotFoundException> response = handler.handleProductNotFoundException(mockException);

        Mockito.verify(mockException).getHttpStatus();
        Mockito.verify(mockException).getMessage();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), sameInstance(mockException));
        assertLogs(logListener.getEvents(), "product_not_found.json", getClass());
    }
}