package ru.yandex.practicum.commerce.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.exception.OrderPaymentAlreadyExistsException;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentState;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.commerce.payment.service.OrderService;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.commerce.payment.service.ProductService;
import ru.yandex.practicum.commerce.payment.util.UUIDGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final int COST_SCALE = 2;
    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(10L, COST_SCALE);

    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentRepository repository;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Payment createPayment(final OrderDto order) {
        requirePaymentNotExist(order.getOrderId());
        Payment payment = extractPayment(order);
        payment = repository.save(payment);
        log.info("Created new payment: orderId = {}, paymentId = {}", payment.getOrderId(), payment.getPaymentId());
        log.debug("Created payment = {}", payment);
        return payment;
    }

    @Override
    public BigDecimal calculateProductCost(final OrderDto order) {
        BigDecimal productCost = BigDecimal.valueOf(0L, COST_SCALE);
        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            ProductDto product = getProductById(entry.getKey());
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            productCost = productCost.add(product.getPrice().multiply(quantity));
        }
        return productCost;
    }

    @Override
    public BigDecimal calculateTotalCost(final OrderDto order) {
        requireProductPriceSet(order);
        requireDeliveryPriceSet(order);
        final BigDecimal fee = order.getProductPrice().multiply(TAX_RATE).setScale(COST_SCALE, RoundingMode.HALF_UP);
        return order.getProductPrice().add(fee).add(order.getDeliveryPrice());
    }

    @Override
    public void confirmPayment(final UUID orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        payment.setState(PaymentState.SUCCESS);
        payment = repository.save(payment);
        orderService.paymentSuccess(orderId);
        log.info("Received payment: orderId = {}, paymentId = {}", orderId, payment.getPaymentId());
        log.debug("Successful payment = {}", payment);
    }

    @Override
    public void signalPaymentFailure(final UUID orderId) {
        Payment payment = getPaymentByOrderId(orderId);
        payment.setState(PaymentState.FAILED);
        payment = repository.save(payment);
        orderService.paymentFailed(orderId);
        log.info("Failed to receive payment: orderId = {}, paymentId = {}", orderId, payment.getPaymentId());
        log.debug("Failed payment = {}", payment);
    }

    private void requirePaymentNotExist(final UUID orderId) {
        if (repository.existsByOrderId((orderId))) {
            throw new OrderPaymentAlreadyExistsException("Payment for order %s already exists".formatted(orderId));
        }
    }

    private Payment extractPayment(OrderDto order) {
        Payment payment = new Payment();
        payment.setPaymentId(uuidGenerator.getNewUUID());
        payment.setOrderId(order.getOrderId());
        payment.setTotalPayment(order.getTotalPrice());
        payment.setProductTotal(order.getProductPrice());
        payment.setDeliveryTotal(order.getDeliveryPrice());
        payment.setFeeTotal(order.getTotalPrice()
                .subtract(order.getProductPrice())
                .subtract(order.getDeliveryPrice()));
        payment.setState(PaymentState.PENDING);
        return payment;
    }

    private void requireProductPriceSet(final OrderDto order) {
        if (order.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Product total not set for order " + order.getOrderId());
        }
    }

    private void requireDeliveryPriceSet(final OrderDto order) {
        if (order.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Delivery cost not set for order " + order.getOrderId());
        }
    }

    private ProductDto getProductById(final UUID productId) {
        try {
            return productService.getProduct(productId);
        } catch (ProductNotFoundException e) {
            throw new NotEnoughInfoInOrderToCalculateException("Price not found for product " + productId);
        }
    }

    private Payment getPaymentByOrderId(final UUID orderId) {
        return repository.findByOrderId(orderId).orElseThrow(
                () -> new NoPaymentFoundException("Payment for order %s does not exist".formatted(orderId))
        );
    }
}
