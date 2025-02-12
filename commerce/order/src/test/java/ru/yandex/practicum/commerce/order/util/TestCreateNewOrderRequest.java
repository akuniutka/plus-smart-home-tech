package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;

public final class TestCreateNewOrderRequest {

    private TestCreateNewOrderRequest() {
        throw new AssertionError();
    }

    public static CreateNewOrderRequest create() {
        final CreateNewOrderRequest request = new CreateNewOrderRequest();
        request.setShoppingCart(TestShoppingCartDto.create());
        request.setDeliveryAddress(TestAddressDto.create());
        return request;
    }
}
