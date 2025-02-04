package ru.yandex.practicum.commerce.exception.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.yandex.practicum.commerce.exception.ApiException;
import ru.yandex.practicum.commerce.exception.ApiExceptions;

import java.io.InputStream;
import java.util.Collection;

public class ApiExceptionDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(final String methodKay, final Response response) {
        final Collection<String> exceptionClassNames = response.headers().get(ApiExceptions.API_EXCEPTION_HEADER);
        if (exceptionClassNames == null || exceptionClassNames.size() != 1) {
            return defaultDecoder.decode(methodKay, response);
        }
        final String exceptionClassName = exceptionClassNames.iterator().next();
        final Class<? extends ApiException> clazz = ApiExceptions.EXCEPTIONS.get(exceptionClassName);
        if (clazz == null) {
            return defaultDecoder.decode(methodKay, response);
        }
        try (InputStream body = response.body().asInputStream()) {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, clazz);
        } catch (Exception e) {
            return e;
        }
    }
}
