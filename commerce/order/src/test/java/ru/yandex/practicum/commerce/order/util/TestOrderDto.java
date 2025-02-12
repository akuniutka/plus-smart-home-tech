package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;

import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_FRAGILE;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_FRAGILE_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_ID_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_PRICE_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_VOLUME;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_VOLUME_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_WEIGHT;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_WEIGHT_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAYMENT_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAYMENT_ID_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.PRODUCT_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.PRODUCT_PRICE_OTHER;
import static ru.yandex.practicum.commerce.order.util.TestModels.TOTAL_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TOTAL_PRICE_OTHER;

public final class TestOrderDto {

    private TestOrderDto() {
        throw new AssertionError();
    }

    public static OrderDto create() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID);
        dto.setShoppingCartId(TestShoppingCartDto.SHOPPING_CART_ID);
        dto.setProducts(TestShoppingCartDto.PRODUCTS);
        dto.setState(OrderState.NEW);
        return dto;
    }

    public static OrderDto withDeliveryParams() {
        final OrderDto dto = create();
        dto.setDeliveryWeight(DELIVERY_WEIGHT);
        dto.setDeliveryVolume(DELIVERY_VOLUME);
        dto.setFragile(DELIVERY_FRAGILE);
        return dto;
    }

    public static OrderDto withProductPrice() {
        final OrderDto dto = withDeliveryParams();
        dto.setProductPrice(PRODUCT_PRICE);
        return dto;
    }

    public static OrderDto withDeliveryPrice() {
        final OrderDto dto = withProductPrice();
        dto.setDeliveryPrice(DELIVERY_PRICE);
        return dto;
    }

    public static OrderDto withTotalPrice() {
        final OrderDto dto = withDeliveryPrice();
        dto.setTotalPrice(TOTAL_PRICE);
        return dto;
    }

    public static OrderDto withPaymentAndDelivery() {
        final OrderDto dto = withTotalPrice();
        dto.setPaymentId(PAYMENT_ID);
        dto.setDeliveryId(DELIVERY_ID);
        return dto;
    }

    public static OrderDto assembled() {
        final OrderDto dto = withPaymentAndDelivery();
        dto.setState(OrderState.ASSEMBLED);
        return dto;
    }

    public static OrderDto withAssemblyFailed() {
        final OrderDto dto = withPaymentAndDelivery();
        dto.setState(OrderState.ASSEMBLY_FAILED);
        return dto;
    }

    public static OrderDto paid() {
        final OrderDto dto = assembled();
        dto.setState(OrderState.PAID);
        return dto;
    }

    public static OrderDto withPaymentFailed() {
        final OrderDto dto = assembled();
        dto.setState(OrderState.PAYMENT_FAILED);
        return dto;
    }

    public static OrderDto delivered() {
        final OrderDto dto = paid();
        dto.setState(OrderState.DELIVERED);
        return dto;
    }

    public static OrderDto withDeliveryFailed() {
        final OrderDto dto = paid();
        dto.setState(OrderState.DELIVERY_FAILED);
        return dto;
    }

    public static OrderDto completed() {
        final OrderDto dto = delivered();
        dto.setState(OrderState.COMPLETED);
        return dto;
    }

    public static OrderDto other() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID_OTHER);
        dto.setShoppingCartId(TestShoppingCartDto.SHOPPING_CART_ID_OTHER);
        dto.setProducts(TestShoppingCartDto.PRODUCTS_OTHER);
        dto.setPaymentId(PAYMENT_ID_OTHER);
        dto.setDeliveryId(DELIVERY_ID_OTHER);
        dto.setState(OrderState.ASSEMBLED);
        dto.setDeliveryWeight(DELIVERY_WEIGHT_OTHER);
        dto.setDeliveryVolume(DELIVERY_VOLUME_OTHER);
        dto.setFragile(DELIVERY_FRAGILE_OTHER);
        dto.setTotalPrice(TOTAL_PRICE_OTHER);
        dto.setDeliveryPrice(DELIVERY_PRICE_OTHER);
        dto.setProductPrice(PRODUCT_PRICE_OTHER);
        return dto;
    }
}
