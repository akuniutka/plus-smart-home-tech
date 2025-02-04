package ru.yandex.practicum.commerce.exception.handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.commerce.exception.ApiException;
import ru.yandex.practicum.commerce.exception.ApiExceptions;
import ru.yandex.practicum.commerce.util.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static ru.yandex.practicum.commerce.util.TestUtils.assertLogs;

class BaseExceptionHandlerTest {

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";
    private static final LogListener logListener = new LogListener(BaseExceptionHandler.class);
    private ApiException mockException;

    private BaseExceptionHandler handler;

    @BeforeEach
    void setUp() {
        mockException = Mockito.mock(ApiException.class);
        logListener.startListen();
        logListener.reset();
        handler = new BaseExceptionHandler();
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockException);
    }

    @Test
    void whenHandleApiException_ThanWrapItInResponseEntityAndReturnAndLof() throws Exception {
        Mockito.when(this.mockException.getHttpStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        Mockito.when(this.mockException.getMessage()).thenReturn(TEST_EXCEPTION_MESSAGE);

        final ResponseEntity<ApiException> response = handler.handleApiException(this.mockException);

        Mockito.verify(mockException).getHttpStatus();
        Mockito.verify(mockException).getMessage();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getHeaders().keySet(), contains(ApiExceptions.API_EXCEPTION_HEADER));
        assertThat(response.getHeaders().get(ApiExceptions.API_EXCEPTION_HEADER),
                contains(ApiException.class.getSimpleName()));
        assertThat(response.getBody(), sameInstance(mockException));
        assertLogs(logListener.getEvents(), "api_exception.json", getClass());
    }
}