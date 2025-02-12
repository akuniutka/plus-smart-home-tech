package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.order.model.Order;

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
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME;

public final class TestOrder {

    private TestOrder() {
        throw new AssertionError();
    }

    public static Order create() {
        final Order order = new Order();
        order.setOrderId(ORDER_ID);
        order.setUsername(USERNAME);
        order.setShoppingCartId(TestShoppingCartDto.SHOPPING_CART_ID);
        order.setProducts(TestShoppingCartDto.PRODUCTS);
        order.setState(OrderState.NEW);
        order.setDeliveryAddress(TestAddress.create());
        return order;
    }

    public static Order withDeliveryParams() {
        final Order order = create();
        order.setDeliveryWeight(DELIVERY_WEIGHT);
        order.setDeliveryVolume(DELIVERY_VOLUME);
        order.setFragile(DELIVERY_FRAGILE);
        return order;
    }

    public static Order withProductPrice() {
        final Order order = withDeliveryParams();
        order.setProductPrice(PRODUCT_PRICE);
        return order;
    }

    public static Order withDeliveryPrice() {
        final Order order = withProductPrice();
        order.setDeliveryPrice(DELIVERY_PRICE);
        return order;
    }

    public static Order withTotalPrice() {
        final Order order = withDeliveryPrice();
        order.setTotalPrice(TOTAL_PRICE);
        return order;
    }

    public static Order withPaymentAndDelivery() {
        final Order order = withTotalPrice();
        order.setPaymentId(PAYMENT_ID);
        order.setDeliveryId(DELIVERY_ID);
        return order;
    }

    public static Order assembled() {
        final Order order = withPaymentAndDelivery();
        order.setState(OrderState.ASSEMBLED);
        return order;
    }

    public static Order withAssemblyFailed() {
        final Order order = withPaymentAndDelivery();
        order.setState(OrderState.ASSEMBLY_FAILED);
        return order;
    }

    public static Order paid() {
        final Order order = assembled();
        order.setState(OrderState.PAID);
        return order;
    }

    public static Order withPaymentFailed() {
        final Order order = assembled();
        order.setState(OrderState.PAYMENT_FAILED);
        return order;
    }

    public static Order delivered() {
        final Order order = paid();
        order.setState(OrderState.DELIVERED);
        return order;
    }

    public static Order withDeliveryFailed() {
        final Order order = paid();
        order.setState(OrderState.DELIVERY_FAILED);
        return order;
    }

    public static Order completed() {
        final Order order = delivered();
        order.setState(OrderState.COMPLETED);
        return order;
    }

    public static Order other() {
        final Order order = new Order();
        order.setOrderId(ORDER_ID_OTHER);
        order.setUsername(USERNAME);
        order.setShoppingCartId(TestShoppingCartDto.SHOPPING_CART_ID_OTHER);
        order.setProducts(TestShoppingCartDto.PRODUCTS_OTHER);
        order.setPaymentId(PAYMENT_ID_OTHER);
        order.setDeliveryId(DELIVERY_ID_OTHER);
        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryAddress(TestAddress.other());
        order.setDeliveryWeight(DELIVERY_WEIGHT_OTHER);
        order.setDeliveryVolume(DELIVERY_VOLUME_OTHER);
        order.setFragile(DELIVERY_FRAGILE_OTHER);
        order.setTotalPrice(TOTAL_PRICE_OTHER);
        order.setDeliveryPrice(DELIVERY_PRICE_OTHER);
        order.setProductPrice(PRODUCT_PRICE_OTHER);
        return order;
    }
}
