package ru.yandex.practicum.commerce.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
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
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;

    @PutMapping
    public OrderDto createOrder(@RequestParam final String username,
            @RequestBody @Valid final CreateNewOrderRequest request) {
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

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable final UUID orderId) {
        log.info("Received request for order: orderId = {}", orderId);
        final Order order = orderService.getOrderById(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with requested order: orderId = {}", orderId);
        log.debug("Requested order = {}", dto);
        return dto;
    }

    @GetMapping
    public List<OrderDto> getOrdersByUsername(@RequestParam final String username, final Pageable pageable) {
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

    @PostMapping("/assembly")
    public OrderDto confirmAssembly(@RequestBody final UUID orderId) {
        log.info("Received request to mark order as successfully assembled: orderId = {}", orderId);
        final Order order = orderService.confirmAssembly(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully assembled order: orderId = {}", orderId);
        log.debug("Assembled order = {}", dto);
        return dto;
    }

    @PostMapping("/assembly/failed")
    public OrderDto setAssemblyFailed(@RequestBody final UUID orderId) {
        log.info("Received request to save assembly failure for order: orderId = {}", orderId);
        final Order order = orderService.setAssemblyFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with not assembled order: orderId = {}", orderId);
        log.debug("Not assembled order = {}", dto);
        return dto;
    }

    @PostMapping("/payment")
    public OrderDto confirmPayment(@RequestBody final UUID orderId) {
        log.info("Received request to mark order as successfully paid: orderId = {}", orderId);
        final Order order = orderService.confirmPayment(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully paid order: orderId = {}, paymentId = {}", orderId, dto.getPaymentId());
        log.debug("Paid order = {}", dto);
        return dto;
    }

    @PostMapping("/payment/failed")
    public OrderDto setPaymentFailed(@RequestBody final UUID orderId) {
        log.info("Received request to save payment failure for order: orderId = {}", orderId);
        final Order order = orderService.setPaymentFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with unpaid order: orderId = {}, paymentId = {}", orderId, dto.getPaymentId());
        log.debug("Unpaid order = {}", dto);
        return dto;
    }

    @PostMapping("/delivery")
    public OrderDto confirmDelivery(@RequestBody final UUID orderId) {
        log.info("Received request to mark order as successfully delivered: orderId = {}", orderId);
        final Order order = orderService.confirmDelivery(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with successfully delivered order: orderId = {}, deliveryId = {}", orderId,
                dto.getDeliveryId());
        log.debug("Delivered order = {}", dto);
        return dto;
    }

    @PostMapping("/delivery/failed")
    public OrderDto setDeliveryFailed(@RequestBody final UUID orderId) {
        log.info("Received request to save delivery failure for order: orderId = {}", orderId);
        final Order order = orderService.setDeliveryFailed(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with not delivered order: orderId = {}, deliveryId = {}", orderId, dto.getDeliveryId());
        log.debug("Not delivered order = {}", dto);
        return dto;
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody final UUID orderId) {
        log.info("Received request to complete order: orderId = {}", orderId);
        final Order order = orderService.completeOrder(orderId);
        final OrderDto dto = orderMapper.mapToDto(order);
        log.info("Responded with completed order: orderId = {}", orderId);
        log.debug("Completed order = {}", dto);
        return dto;
    }
}
