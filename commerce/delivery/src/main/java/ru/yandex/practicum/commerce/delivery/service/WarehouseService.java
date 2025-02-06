package ru.yandex.practicum.commerce.delivery.service;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;

@FeignClient(name = "warehouse")
public interface WarehouseService {

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request);
}
