package ru.yandex.practicum.commerce.order.util;

import ru.yandex.practicum.commerce.order.model.Address;

import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_CITY;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_COUNTRY;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_FLAT;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_HOUSE;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_STREET;

public final class TestAddress {

    private static final String DELIVERY_COUNTRY_OTHER = "NOLAND";
    private static final String DELIVERY_CITY_OTHER = "STONE CITY";
    private static final String DELIVERY_STREET_OTHER = "FIRST LANE";
    private static final String DELIVERY_HOUSE_OTHER = "11";
    private static final String DELIVERY_FLAT_OTHER = "2";

    private TestAddress() {
        throw new AssertionError();
    }

    public static Address create() {
        final Address address = new Address();
        address.setCountry(DELIVERY_COUNTRY);
        address.setCity(DELIVERY_CITY);
        address.setStreet(DELIVERY_STREET);
        address.setHouse(DELIVERY_HOUSE);
        address.setFlat(DELIVERY_FLAT);
        return address;
    }

    public static Address other() {
        final Address address = new Address();
        address.setCountry(DELIVERY_COUNTRY_OTHER);
        address.setCity(DELIVERY_CITY_OTHER);
        address.setStreet(DELIVERY_STREET_OTHER);
        address.setHouse(DELIVERY_HOUSE_OTHER);
        address.setFlat(DELIVERY_FLAT_OTHER);
        return address;
    }
}
