package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;

import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_WEIGHT;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_VOLUME;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_FRAGILE;

public final class TestBookedProductsDto {

    private TestBookedProductsDto() {
        throw new AssertionError();
    }

    public static BookedProductsDto create() {
        final BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(DELIVERY_WEIGHT);
        dto.setDeliveryVolume(DELIVERY_VOLUME);
        dto.setFragile(DELIVERY_FRAGILE);
        return dto;
    }
}
