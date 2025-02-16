package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.PaymentOperations;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(final OrderDto order) {
        log.info("Received request to create new payment: orderId = {}", order.getOrderId());
        log.debug("Order for new payment = {}", order);
        final Payment payment = paymentService.createPayment(order);
        final PaymentDto dto = paymentMapper.mapToDto(payment);
        log.info("Responded with created payment: orderId = {}, paymentId = {}", payment.getOrderId(),
                dto.getPaymentId());
        log.debug("Created payment = {}", dto);
        return dto;
    }

    @Override
    public BigDecimal calculateProductCost(final OrderDto order) {
        log.info("Received request to calculate products cost: orderId = {}", order.getOrderId());
        log.debug("Order to calculate products cost = {}", order);
        final BigDecimal productCost = paymentService.calculateProductCost(order);
        log.info("Responded with products cost: orderId = {}, productCost = {}", order.getOrderId(), productCost);
        return productCost;
    }

    @Override
    public BigDecimal calculateTotalCost(final OrderDto order) {
        log.info("Received request to calculate total order cost: orderId = {}", order.getOrderId());
        log.debug("Order to calculate total cost = {}", order);
        final BigDecimal totalCost = paymentService.calculateTotalCost(order);
        log.info("Responded with total order cost: orderId = {}, totalCost = {}", order.getOrderId(), totalCost);
        return totalCost;
    }

    @Override
    public void confirmPayment(final UUID orderId) {
        log.info("Received request to set payment as successful: orderId = {}", orderId);
        paymentService.confirmPayment(orderId);
        log.info("Responded with 200 OK to set payment as successful: orderId = {}", orderId);
    }

    @Override
    public void signalPaymentFailure(final UUID orderId) {
        log.info("Received request to set payment as failed: orderId = {}", orderId);
        paymentService.signalPaymentFailure(orderId);
        log.info("Responded with 200 OK to set payment as failed: orderId = {}", orderId);
    }
}
