package ru.yandex.practicum.commerce.cart.configuration;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.commerce.exception.decoder.ApiExceptionDecoder;

@Configuration
public class ShoppingCartConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ApiExceptionDecoder();
    }
}
