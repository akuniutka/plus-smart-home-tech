package ru.yandex.practicum.commerce.payment.util;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductState;
import ru.yandex.practicum.commerce.dto.store.QuantityState;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class TestModels {

    public static final int COST_SCALE = 2;
    public static final UUID PAYMENT_ID = UUID.fromString("ff035258-939e-4f09-8119-61459eb68949");
    public static final UUID ORDER_ID_A = UUID.fromString("f8991fc5-1c29-4395-b781-7717893cea92");
    public static final UUID ORDER_ID_B = UUID.fromString("2b8dc08c-af0a-422f-b54b-eb2157e8bf59");

    public static final BigDecimal TOTAL_COST = BigDecimal.valueOf(5437L, COST_SCALE);
    public static final BigDecimal PRODUCT_COST = BigDecimal.valueOf(1997L, COST_SCALE);
    public static final BigDecimal DELIVERY_COST = BigDecimal.valueOf(3240L, COST_SCALE);
    public static final BigDecimal TAX_VALUE = BigDecimal.valueOf(200L, COST_SCALE);

    public static final UUID PRODUCT_ID_A = UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25");
    public static final String PRODUCT_NAME_A = "light sensor v.1";
    public static final String DESCRIPTION_A = "A modern energy efficient light sensor";
    public static final String IMAGE_SRC_A = "/sensors/light/light-sensor-v1.jpg";
    public static final QuantityState QUANTITY_A = QuantityState.ENOUGH;
    public static final ProductState PRODUCT_STATE_A = ProductState.ACTIVE;
    public static final ProductCategory CATEGORY_A = ProductCategory.SENSORS;
    public static final BigDecimal PRICE_A = BigDecimal.valueOf(999, 2);

    public static final UUID PRODUCT_ID_B = UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808");
    public static final String PRODUCT_NAME_B = "lighting device v.2";
    public static final String DESCRIPTION_B = "A new version of smart lighting device";
    public static final String IMAGE_SRC_B = "/lighting/lighting-device-v2.jpg";
    public static final QuantityState QUANTITY_B = QuantityState.FEW;
    public static final ProductState PRODUCT_STATE_B = ProductState.DEACTIVATE;
    public static final ProductCategory CATEGORY_B = ProductCategory.LIGHTING;
    public static final BigDecimal PRICE_B = BigDecimal.valueOf(499, 2);

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    private TestModels() {
        throw new AssertionError();
    }

    public static Payment getTestPayment() {
        final Payment payment = new Payment();
        payment.setPaymentId(PAYMENT_ID);
        payment.setOrderId(ORDER_ID_A);
        payment.setTotalPayment(TOTAL_COST);
        payment.setProductTotal(PRODUCT_COST);
        payment.setDeliveryTotal(DELIVERY_COST);
        payment.setFeeTotal(TAX_VALUE);
        payment.setState(PaymentState.PENDING);
        return payment;
    }

    public static Payment getTestPaymentSuccessful() {
        final Payment payment = getTestPayment();
        payment.setState(PaymentState.SUCCESS);
        return payment;
    }

    public static Payment getTestPaymentFailed() {
        final Payment payment = getTestPayment();
        payment.setState(PaymentState.FAILED);
        return payment;
    }

    public static PaymentDto getTestPaymentDto() {
        final PaymentDto dto = new PaymentDto();
        dto.setPaymentId(PAYMENT_ID);
        dto.setTotalPayment(TOTAL_COST);
        dto.setDeliveryTotal(DELIVERY_COST);
        dto.setFeeTotal(TAX_VALUE);
        return dto;
    }

    public static OrderDto getTestOrderDtoWithoutCosts() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID_A);
        dto.setShoppingCartId(UUID.fromString("3771b0ec-41ec-4c28-80e1-d28b7092df8c"));
        dto.setProducts(getTestProducts());
        dto.setDeliveryId(UUID.fromString("1c44948b-78ca-48a5-80e5-6a901af8c117"));
        dto.setState(OrderState.NEW);
        dto.setDeliveryWeight(BigDecimal.valueOf(14000L, 3));
        dto.setDeliveryVolume(BigDecimal.valueOf(54000L, 3));
        dto.setFragile(true);
        return dto;
    }

    public static OrderDto getTestOrderDtoWithoutTotalCost() {
        final OrderDto dto = getTestOrderDtoWithoutCosts();
        dto.setDeliveryPrice(DELIVERY_COST);
        dto.setProductPrice(PRODUCT_COST);
        return dto;
    }

    public static OrderDto getTestOrderDto() {
        final OrderDto dto = getTestOrderDtoWithoutTotalCost();
        dto.setTotalPrice(TOTAL_COST);
        return dto;
    }

    public static OrderDto getTestOrderDtoPaid() {
        final OrderDto order = getTestOrderDto();
        order.setState(OrderState.PAID);
        return order;
    }

    public static OrderDto getTestOrderDtoNotPaid() {
        final OrderDto order = getTestOrderDto();
        order.setState(OrderState.PAYMENT_FAILED);
        return order;
    }

    private static Map<UUID, Long> getTestProducts() {
        return Map.of(
                UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25"), 1L,
                UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808"), 2L
        );
    }

    public static ProductDto getTestProductDtoA() {
        final ProductDto dto = new ProductDto();
        dto.setProductId(PRODUCT_ID_A);
        dto.setProductName(PRODUCT_NAME_A);
        dto.setDescription(DESCRIPTION_A);
        dto.setImageSrc(IMAGE_SRC_A);
        dto.setQuantityState(QUANTITY_A);
        dto.setProductState(PRODUCT_STATE_A);
        dto.setProductCategory(CATEGORY_A);
        dto.setPrice(PRICE_A);
        return dto;
    }

    public static ProductDto getTestProductDtoB() {
        final ProductDto dto = new ProductDto();
        dto.setProductId(PRODUCT_ID_B);
        dto.setProductName(PRODUCT_NAME_B);
        dto.setDescription(DESCRIPTION_B);
        dto.setImageSrc(IMAGE_SRC_B);
        dto.setQuantityState(QUANTITY_B);
        dto.setProductState(PRODUCT_STATE_B);
        dto.setProductCategory(CATEGORY_B);
        dto.setPrice(PRICE_B);
        return dto;
    }
}
