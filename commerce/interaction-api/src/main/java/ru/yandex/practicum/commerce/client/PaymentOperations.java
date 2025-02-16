package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto order);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto order);

    @PostMapping("/refund")
    void confirmPayment(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void signalPaymentFailure(@RequestBody UUID orderId);
}
