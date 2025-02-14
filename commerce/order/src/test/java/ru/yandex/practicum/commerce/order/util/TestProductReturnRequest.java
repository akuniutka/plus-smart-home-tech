package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;

import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID;


public final class TestProductReturnRequest {

    private TestProductReturnRequest() {
        throw new AssertionError();
    }

    public static ProductReturnRequest create() {
        final ProductReturnRequest request = new ProductReturnRequest();
        request.setOrderId(ORDER_ID);
        request.setProducts(TestShoppingCartDto.PRODUCTS);
        return request;
    }
}
