package ru.yandex.practicum.commerce.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.OrderOperations;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.store.Pageable;
import ru.yandex.practicum.commerce.order.mapper.AddressMapper;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Address;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderOperations {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    @Override
    public OrderDto createOrder(final String username, final CreateNewOrderRequest request) {
        log.info("Received request to create order: username = {}, shoppingCartId = {}", username,
                request.getShoppingCart().getShoppingCartId());
        log.debug("Create new order request = {}", request);
        final ShoppingCartDto shoppingCart = request.getShoppingCart();
        final Address deliveryAddress = addressMapper.mapToEntity(request.getDeliveryAddress());
        final Order order = orderService.addNewOrder(username, shoppingCart, deliveryAddress);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with created order: orderId = {}, username = {}, shoppingCartId = {}", dto.getOrderId(),
                username, dto.getShoppingCartId());
        log.debug("Created order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto getOrderById(final UUID orderId) {
        log.info("Received request for order: orderId = {}", orderId);
        final Order order = orderService.getOrderById(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with requested order: orderId = {}", orderId);
        log.debug("Requested order = {}", dto);
        return dto;
    }

    @Override
    public List<OrderDto> getOrdersByUsername(final String username, final Pageable pageable) {
        log.info("Received request to get user's orders: username = {}", username);
        log.debug("Requested page = {}, page size = {}, sort by {}", pageable.getPage(), pageable.getSize(),
                pageable.getSort());
        final PageRequest page = PageRequest.of(pageable.getPage(), pageable.getSize(), Sort.by(pageable.getSort()));
        final List<Order> orders = orderService.findOrdersByUsername(username, page);
        final List<OrderDto> dtos = orderMapper.mapToDto(orders);
        log.info("Responded with user's orders: username = {}", username);
        log.debug("Requested orders = {}", dtos);
        return dtos;
    }

    @Override
    public OrderDto calculateProductCost(final UUID orderId) {
        log.info("Received request to calculate order product cost: orderId = {}", orderId);
        final Order order = orderService.calculateProductCost(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with updated order: orderId = {}, productPrice = {}", orderId, dto.getProductPrice());
        log.debug("Order with product cost = {}", dto);
        return dto;
    }

    @Override
    public OrderDto calculateDeliveryCost(final UUID orderId) {
        log.info("Received request to calculate order delivery cost: orderId = {}", orderId);
        final Order order = orderService.calculateDeliveryCost(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with updated order: orderId = {}, deliveryPrice = {}", orderId, dto.getDeliveryPrice());
        log.debug("Order with delivery cost = {}", dto);
        return dto;
    }

    @Override
    public OrderDto calculateTotalCost(final UUID orderId) {
        log.info("Received request to calculate order total cost: orderId = {}", orderId);
        final Order order = orderService.calculateTotalCost(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with updated order: orderId = {}, totalPrice = {}", orderId, dto.getTotalPrice());
        log.debug("Order with total cost = {}", dto);
        return dto;
    }

    @Override
    public OrderDto confirmAssembly(final UUID orderId) {
        log.info("Received request to mark order as successfully assembled: orderId = {}", orderId);
        final Order order = orderService.confirmAssembly(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully assembled order: orderId = {}", orderId);
        log.debug("Assembled order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto setAssemblyFailed(final UUID orderId) {
        log.info("Received request to save assembly failure for order: orderId = {}", orderId);
        final Order order = orderService.setAssemblyFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with not assembled order: orderId = {}", orderId);
        log.debug("Not assembled order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto confirmPayment(final UUID orderId) {
        log.info("Received request to mark order as successfully paid: orderId = {}", orderId);
        final Order order = orderService.confirmPayment(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully paid order: orderId = {}, paymentId = {}", orderId, dto.getPaymentId());
        log.debug("Paid order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto setPaymentFailed(final UUID orderId) {
        log.info("Received request to save payment failure for order: orderId = {}", orderId);
        final Order order = orderService.setPaymentFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with unpaid order: orderId = {}, paymentId = {}", orderId, dto.getPaymentId());
        log.debug("Unpaid order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto confirmDelivery(final UUID orderId) {
        log.info("Received request to mark order as successfully delivered: orderId = {}", orderId);
        final Order order = orderService.confirmDelivery(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully delivered order: orderId = {}, deliveryId = {}", orderId,
                dto.getDeliveryId());
        log.debug("Delivered order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto setDeliveryFailed(final UUID orderId) {
        log.info("Received request to save delivery failure for order: orderId = {}", orderId);
        final Order order = orderService.setDeliveryFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with not delivered order: orderId = {}, deliveryId = {}", orderId, dto.getDeliveryId());
        log.debug("Not delivered order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto returnProducts(final ProductReturnRequest request) {
        log.info("Received request to return order: orderId = {}", request.getOrderId());
        log.debug("Return request = {}", request);
        final Order order = orderService.returnProducts(request);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with returned order: orderId = {}", dto.getOrderId());
        log.debug("Returned order = {}", dto);
        return dto;
    }

    @Override
    public OrderDto completeOrder(final UUID orderId) {
        log.info("Received request to complete order: orderId = {}", orderId);
        final Order order = orderService.completeOrder(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with completed order: orderId = {}", orderId);
        log.debug("Completed order = {}", dto);
        return dto;
    }
}
