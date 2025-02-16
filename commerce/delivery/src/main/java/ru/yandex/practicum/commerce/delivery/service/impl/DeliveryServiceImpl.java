package ru.yandex.practicum.commerce.delivery.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.delivery.model.Address;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.delivery.client.OrderClient;
import ru.yandex.practicum.commerce.delivery.client.WarehouseClient;
import ru.yandex.practicum.commerce.delivery.util.UUIDGenerator;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.exception.OrderDeliveryAlreadyExistsException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private static final int COST_SCALE = 2;
    private static final BigDecimal BASE_COST = BigDecimal.valueOf(500L, COST_SCALE);
    private static final BigDecimal WAREHOUSE_COEFFICIENT = BigDecimal.valueOf(200L, COST_SCALE);
    private static final BigDecimal FRAGILE_COEFFICIENT = BigDecimal.valueOf(20L, COST_SCALE);
    private static final BigDecimal WEIGHT_COEFFICIENT = BigDecimal.valueOf(30L, COST_SCALE);
    private static final BigDecimal VOLUME_COEFFICIENT = BigDecimal.valueOf(20L, COST_SCALE);
    private static final BigDecimal DESTINATION_COEFFICIENT = BigDecimal.valueOf(20L, COST_SCALE);

    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryRepository repository;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Delivery planDelivery(final Delivery delivery) {
        requireDeliveryNotExist(delivery.getOrderId());
        delivery.setDeliveryId(uuidGenerator.getNewUUID());
        final Delivery savedDelivery = repository.save(delivery);
        log.info("Planned new delivery: orderId = {}, deliveryId = {}", savedDelivery.getOrderId(),
                savedDelivery.getDeliveryId());
        log.debug("Planned delivery = {}", savedDelivery);
        return savedDelivery;
    }

    @Override
    public void pickDelivery(final UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        delivery = repository.save(delivery);
        final ShippedToDeliveryRequest request = createShippedToDeliveryRequest(orderId, delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(request);
        orderClient.confirmAssembly(orderId);
        log.info("Picked delivery: orderId = {}, deliveryId = {}", orderId, delivery.getDeliveryId());
        log.debug("Picked delivery = {}", delivery);
    }

    @Override
    public void confirmDelivery(final UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        delivery = repository.save(delivery);
        orderClient.confirmDelivery(orderId);
        log.info("Delivered order: orderId = {}, deliveryId = {}", orderId, delivery.getDeliveryId());
        log.debug("Successful delivery = {}", delivery);
    }

    @Override
    public void signalDeliveryFailure(final UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        delivery = repository.save(delivery);
        orderClient.setDeliveryFailed(orderId);
        log.info("Failed to deliver order: orderId = {}, deliveryId = {}", orderId, delivery.getDeliveryId());
        log.debug("Failed delivery = {}", delivery);
    }

    @Override
    public BigDecimal calculateDeliveryCost(final OrderDto order) {
        final Delivery delivery = getDeliveryByOrderId(order.getOrderId());
        BigDecimal totalCost = BASE_COST;
        totalCost = totalCost.add(getWarehouseCost(totalCost, delivery.getFromAddress()));
        totalCost = totalCost.add(getFragileCost(totalCost, order.getFragile()));
        totalCost = totalCost.add(getWeightCost(order.getDeliveryWeight()));
        totalCost = totalCost.add(getVolumeCost(order.getDeliveryVolume()));
        totalCost = totalCost.add(getDestinationCost(totalCost, delivery.getFromAddress(), delivery.getToAddress()));
        return totalCost;
    }

    private void requireDeliveryNotExist(final UUID orderId) {
        if (repository.existsByOrderId(orderId)) {
            throw new OrderDeliveryAlreadyExistsException("Delivery for order %s already exists".formatted(orderId));
        }
    }

    private Delivery getDeliveryByOrderId(final UUID orderId) {
        return repository.findByOrderId(orderId).orElseThrow(
                () -> new NoDeliveryFoundException("Delivery for order %s does not exist".formatted(orderId))
        );
    }

    private ShippedToDeliveryRequest createShippedToDeliveryRequest(final UUID orderId, final UUID deliverId) {
        final ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(orderId);
        request.setDeliveryId(deliverId);
        return request;
    }

    private BigDecimal getWarehouseCost(final BigDecimal base, final Address warehouseAddress) {
        if ("ADDRESS_1".equals(warehouseAddress.getCity())) {
            return base;
        } else {
            return base.multiply(WAREHOUSE_COEFFICIENT).setScale(COST_SCALE, RoundingMode.HALF_UP);
        }
    }

    private BigDecimal getFragileCost(final BigDecimal base, final boolean isFragile) {
        if (isFragile) {
            return base.multiply(FRAGILE_COEFFICIENT).setScale(COST_SCALE, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getWeightCost(final BigDecimal weight) {
        return weight.multiply(WEIGHT_COEFFICIENT).setScale(COST_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getVolumeCost(final BigDecimal volume) {
        return volume.multiply(VOLUME_COEFFICIENT).setScale(COST_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal getDestinationCost(final BigDecimal base, final Address warehouseAddress,
            final Address destinationAddress) {
        if (warehouseAddress.getStreet().equals(destinationAddress.getStreet())) {
            return BigDecimal.ZERO;
        } else {
            return base.multiply(DESTINATION_COEFFICIENT).setScale(COST_SCALE, RoundingMode.HALF_UP);
        }
    }
}
