package ru.yandex.practicum.commerce.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryMapper deliveryMapper;

    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid final DeliveryDto dtoIn) {
        log.info("Received request to plan new delivery: orderId = {}", dtoIn.getOrderId());
        log.debug("New delivery = {}", dtoIn);
        final Delivery newDelivery = deliveryMapper.mapToEntity(dtoIn);
        final Delivery plannedDelivery = deliveryService.planDelivery(newDelivery);
        final DeliveryDto dtoOut = deliveryMapper.mapToDto(plannedDelivery);
        log.info("Responded with planned delivery: orderId = {}, deliveryId = {}", dtoOut.getOrderId(),
                dtoOut.getDeliveryId());
        log.debug("Planned delivery = {}", dtoOut);
        return dtoOut;
    }

    @PostMapping("/picked")
    void pickDelivery(@RequestBody final UUID orderId) {
        log.info("Received request to set delivery as picked: orderId = {}", orderId);
        deliveryService.pickDelivery(orderId);
        log.info("Responded with 200 OK to set delivery as picked: orderId = {}", orderId);
    }

    @PostMapping("/successful")
    void confirmDelivery(@RequestBody final UUID orderId) {
        log.info("Received request to set delivery as successful: orderId = {}", orderId);
        deliveryService.confirmDelivery(orderId);
        log.info("Responded with 200 OK to set delivery as successful: orderId = {}", orderId);
    }

    @PostMapping("/failed")
    void signalDeliveryFailure(@RequestBody final UUID orderId) {
        log.info("Received request to set delivery as failed: orderId = {}", orderId);
        deliveryService.signalDeliveryFailure(orderId);
        log.info("Responded with 200 OK to set delivery as failed: orderId = {}", orderId);
    }

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@RequestBody @Valid OrderDto order) {
        log.info("Received request to calculate delivery cost: orderId = {}", order.getOrderId());
        log.debug("Order = {}", order);
        final BigDecimal deliveryCost = deliveryService.calculateDeliveryCost(order);
        log.info("Responded with order delivery cost: orderId = {}, deliveryCost = {}", order.getOrderId(),
                deliveryCost);
        return deliveryCost;
    }
}
