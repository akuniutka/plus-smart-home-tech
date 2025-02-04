package ru.yandex.practicum.commerce.order.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.order.model.Address;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;

public interface OrderService {

    Order addNewOrder(String username, ShoppingCartDto shoppingCart, Address deliveryAddress);

    List<Order> findOrdersByUsername(String username, Pageable pageable);
}
