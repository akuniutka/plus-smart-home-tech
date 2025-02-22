package ru.yandex.practicum.commerce.order.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.model.Address;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order addNewOrder(String username, ShoppingCartDto shoppingCart, Address deliveryAddress);

    Order getOrderById(UUID orderId);

    List<Order> findOrdersByUsername(String username, Pageable pageable);

    Order calculateProductCost(UUID orderId);

    Order calculateDeliveryCost(UUID orderId);

    Order calculateTotalCost(UUID orderId);

    Order confirmAssembly(UUID orderId);

    Order setAssemblyFailed(UUID orderId);

    Order confirmPayment(UUID orderId);

    Order setPaymentFailed(UUID orderId);

    Order confirmDelivery(UUID orderId);

    Order setDeliveryFailed(UUID orderId);

    Order returnProducts(ProductReturnRequest request);

    Order completeOrder(UUID orderId);
}
