package ru.yandex.practicum.commerce.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.commerce.dto.store.ProductDto;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ProductService {

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
