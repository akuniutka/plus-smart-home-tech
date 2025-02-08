package ru.yandex.practicum.commerce.payment.service;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.payment.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    Payment createPayment(OrderDto order);

    BigDecimal calculateProductCost(OrderDto order);

    BigDecimal calculateTotalCost(OrderDto order);

    void confirmPayment(UUID orderId);

    void signalPaymentFailure(UUID orderId);
}
