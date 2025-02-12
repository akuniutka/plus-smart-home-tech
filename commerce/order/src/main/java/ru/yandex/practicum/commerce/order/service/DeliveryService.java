package ru.yandex.practicum.commerce.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;

@FeignClient(name = "delivery")
public interface DeliveryService {

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal calculateDeliveryCost(@RequestBody OrderDto order);
}
