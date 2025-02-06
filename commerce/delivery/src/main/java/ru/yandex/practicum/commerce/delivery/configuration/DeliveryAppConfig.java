package ru.yandex.practicum.commerce.delivery.configuration;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.commerce.exception.decoder.ApiExceptionDecoder;

@Configuration
public class DeliveryAppConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiExceptionDecoder();
    }
}
