package ru.yandex.practicum.commerce.delivery.service;

import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    Delivery planDelivery(Delivery delivery);

    void pickDelivery(UUID orderId);

    void confirmDelivery(UUID orderId);

    void signalDeliveryFailure(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto order);
}
