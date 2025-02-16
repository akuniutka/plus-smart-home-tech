package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto dto);

    @PostMapping("/picked")
    void pickDelivery(@RequestBody UUID orderId);

    @PostMapping("/successful")
    void confirmDelivery(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void signalDeliveryFailure(@RequestBody UUID orderId);

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@RequestBody @Valid OrderDto order);
}
