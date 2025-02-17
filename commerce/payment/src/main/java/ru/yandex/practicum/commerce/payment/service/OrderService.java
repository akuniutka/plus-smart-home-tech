package ru.yandex.practicum.commerce.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderService {

    @PostMapping("/api/v1/order/payment")
    OrderDto paymentSuccess(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);
}
