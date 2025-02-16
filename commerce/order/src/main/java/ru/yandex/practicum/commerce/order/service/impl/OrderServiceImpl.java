package ru.yandex.practicum.commerce.order.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Address;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.client.DeliveryClient;
import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.commerce.order.client.PaymentClient;
import ru.yandex.practicum.commerce.order.client.WarehouseClient;
import ru.yandex.practicum.commerce.order.util.UUIDGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final WarehouseClient warehouseService;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final OrderRepository repository;
    private final OrderMapper orderMapper;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Order addNewOrder(final String username, final ShoppingCartDto shoppingCart, final Address deliveryAddress) {
        requireUsernameNotBlank(username);
        requireProductsAvailableInWarehouse(shoppingCart);
        Order order = new Order();
        order.setOrderId(uuidGenerator.getNewUUID());
        order.setUsername(username);
        order.setShoppingCartId(shoppingCart.getShoppingCartId());
        order.setProducts(shoppingCart.getProducts());
        order.setDeliveryAddress(deliveryAddress);
        order.setState(OrderState.NEW);
        order = repository.save(order);
        log.info("Created new order: orderId = {}, username = {}, shoppingCartId = {}", order.getOrderId(),
                order.getUsername(), order.getShoppingCartId());
        log.debug("New order = {}", order);
        return order;
    }

    @Override
    public Order getOrderById(final UUID orderId) {
        return repository.findById(orderId).orElseThrow(
                () -> new NoOrderFoundException("Order %s does not exist".formatted(orderId))
        );
    }

    @Override
    public List<Order> findOrdersByUsername(final String username, final Pageable pageable) {
        requireUsernameNotBlank(username);
        return repository.findAllByUsername(username, pageable);
    }

    @Override
    public Order calculateProductCost(final UUID orderId) {
        Order order = getOrderById(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        final BigDecimal productPrice = paymentClient.calculateProductCost(dto);
        order.setProductPrice(productPrice);
        order = repository.save(order);
        log.info("Calculated product price for order: orderId = {}, productPrice = {}", order.getOrderId(),
                order.getProductPrice());
        log.debug("Order with calculated product price = {}", order);
        return order;
    }

    @Override
    public Order calculateDeliveryCost(final UUID orderId) {
        Order order = getOrderById(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        final BigDecimal deliveryPrice = deliveryClient.calculateDeliveryCost(dto);
        order.setDeliveryPrice(deliveryPrice);
        order = repository.save(order);
        log.info("Calculated delivery price for order: orderId = {}, deliveryPrice = {}", order.getOrderId(),
                order.getDeliveryPrice());
        log.debug("Order with calculated delivery price = {}", order);
        return order;
    }

    @Override
    public Order calculateTotalCost(final UUID orderId) {
        Order order = getOrderById(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        final BigDecimal totalPrice = paymentClient.calculateTotalCost(dto);
        order.setTotalPrice(totalPrice);
        order = repository.save(order);
        log.info("Calculated total price for order: orderId = {}, totalPrice = {}", order.getOrderId(),
                order.getTotalPrice());
        log.debug("Order with calculated total price = {}", order);
        return order;
    }

    @Override
    public Order confirmAssembly(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        order = repository.save(order);
        log.info("Marked order as successfully assembled: orderId = {}", orderId);
        log.debug("Assembled order = {}", order);
        return order;
    }

    @Override
    public Order setAssemblyFailed(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        order = repository.save(order);
        log.info("Saved assembly failure for order: orderId = {}", orderId);
        log.debug("Order with assembly failed = {}", order);
        return order;
    }

    @Override
    public Order confirmPayment(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);
        order = repository.save(order);
        log.info("Marked order as successfully paid: orderId = {}, paymentId = {}", orderId, order.getPaymentId());
        log.debug("Paid order = {}", order);
        return order;
    }

    @Override
    public Order setPaymentFailed(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        order = repository.save(order);
        log.info("Saved payment failure for order: orderId = {}, paymentId = {}", orderId, order.getPaymentId());
        log.debug("Order with payment failed = {}", order);
        return order;
    }

    @Override
    public Order confirmDelivery(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        order = repository.save(order);
        log.info("Marked order as successfully delivered: orderId = {}, deliveryId = {}", orderId, order.getDeliveryId());
        log.debug("Delivered order = {}", order);
        return order;
    }

    @Override
    public Order setDeliveryFailed(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        order = repository.save(order);
        log.info("Saved delivery failure for order: orderId = {}, deliveryId = {}", orderId, order.getDeliveryId());
        log.debug("Order with delivery failed = {}", order);
        return order;
    }

    @Override
    public Order returnProducts(final ProductReturnRequest request) {
        Order order = getOrderById(request.getOrderId());
        warehouseService.returnProducts(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        order = repository.save(order);
        log.info("Returned order: orderId = {}", order.getOrderId());
        log.debug("Returned order = {}", order);
        return order;
    }

    @Override
    public Order completeOrder(final UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        order = repository.save(order);
        log.info("Completed order: orderId = {}", orderId);
        log.debug("Completed order = {}", order);
        return order;
    }

    private void requireUsernameNotBlank(final String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("User not authorized");
        }
    }

    private void requireProductsAvailableInWarehouse(final ShoppingCartDto shoppingCart) {
        try {
            warehouseService.checkProductsAvailability(shoppingCart);
        } catch (ProductInShoppingCartNotInWarehouse | ProductInShoppingCartLowQuantityInWarehouse e) {
            throw new NoSpecifiedProductInWarehouseException(e.getUserMessage());
        }
    }
}
