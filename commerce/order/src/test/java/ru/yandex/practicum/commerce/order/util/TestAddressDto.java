package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.dto.delivery.AddressDto;

import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_CITY;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_COUNTRY;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_FLAT;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_HOUSE;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_STREET;

public final class TestAddressDto {

    private TestAddressDto() {
        throw new AssertionError();
    }

    public static AddressDto create() {
        final AddressDto dto = new AddressDto();
        dto.setCountry(DELIVERY_COUNTRY);
        dto.setCity(DELIVERY_CITY);
        dto.setStreet(DELIVERY_STREET);
        dto.setHouse(DELIVERY_HOUSE);
        dto.setFlat(DELIVERY_FLAT);
        return dto;
    }
}
