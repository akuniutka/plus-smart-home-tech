package ru.yandex.practicum.commerce.warehouse.util;

import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;
import ru.yandex.practicum.commerce.warehouse.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public final class TestModels {

    public static final int SCALE = 3;

    public static final UUID PRODUCT_ID_A = UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25");
    public static final boolean PRODUCT_FRAGILE_A = true;
    public static final BigDecimal PRODUCT_WIDTH_A = BigDecimal.valueOf(1000L, SCALE);
    public static final BigDecimal PRODUCT_HEIGHT_A = BigDecimal.valueOf(2000L, SCALE);
    public static final BigDecimal PRODUCT_DEPTH_A = BigDecimal.valueOf(3000L, SCALE);
    public static final BigDecimal PRODUCT_WEIGHT_A = BigDecimal.valueOf(4000L, SCALE);
    public static final long PRODUCT_TOTAL_QUANTITY_A = 10L;
    public static final long PRODUCT_BOOKED_QUANTITY_A = 5L;

    public static final UUID PRODUCT_ID_B = UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808");
    public static final boolean PRODUCT_FRAGILE_B = false;
    public static final BigDecimal PRODUCT_WIDTH_B = BigDecimal.valueOf(2000L, SCALE);
    public static final BigDecimal PRODUCT_HEIGHT_B = BigDecimal.valueOf(3000L, SCALE);
    public static final BigDecimal PRODUCT_DEPTH_B = BigDecimal.valueOf(4000L, SCALE);
    public static final BigDecimal PRODUCT_WEIGHT_B = BigDecimal.valueOf(5000L, SCALE);
    public static final long PRODUCT_TOTAL_QUANTITY_B = 20L;
    public static final long PRODUCT_BOOKED_QUANTITY_B = 15L;

    public static final UUID PRODUCT_ID_C = UUID.fromString("0a53f38d-dd00-4f80-9b2e-c9d17ee46385");

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    private static final String ADDRESS_A = "ADDRESS_1";
    private static final String ADDRESS_B = "ADDRESS_2";

    private TestModels() {
        throw new AssertionError();
    }

    public static Product getTestProductA() {
        final Dimension dimension = new Dimension();
        dimension.setWidth(PRODUCT_WIDTH_A);
        dimension.setHeight(PRODUCT_HEIGHT_A);
        dimension.setDepth(PRODUCT_DEPTH_A);
        final Product product = new Product();
        product.setProductId(PRODUCT_ID_A);
        product.setFragile(PRODUCT_FRAGILE_A);
        product.setDimension(dimension);
        product.setWeight(PRODUCT_WEIGHT_A);
        product.setTotalQuantity(PRODUCT_TOTAL_QUANTITY_A);
        product.setBookedQuantity(PRODUCT_BOOKED_QUANTITY_A);
        return product;
    }

    public static Product getTestProductB() {
        final Dimension dimension = new Dimension();
        dimension.setWidth(PRODUCT_WIDTH_B);
        dimension.setHeight(PRODUCT_HEIGHT_B);
        dimension.setDepth(PRODUCT_DEPTH_B);
        final Product product = new Product();
        product.setProductId(PRODUCT_ID_B);
        product.setFragile(PRODUCT_FRAGILE_B);
        product.setDimension(dimension);
        product.setWeight(PRODUCT_WEIGHT_B);
        product.setTotalQuantity(PRODUCT_TOTAL_QUANTITY_B);
        product.setBookedQuantity(PRODUCT_BOOKED_QUANTITY_B);
        return product;
    }

    public static AddressDto getTestAddressDtoA() {
        return getTestAddress(ADDRESS_A);
    }

    public static AddressDto getTestAddressDtoB() {
        return getTestAddress(ADDRESS_B);
    }

    private static AddressDto getTestAddress(final String addressFiller) {
        final AddressDto address = new AddressDto();
        address.setCountry(addressFiller);
        address.setCity(addressFiller);
        address.setStreet(addressFiller);
        address.setHouse(addressFiller);
        address.setFlat(addressFiller);
        return address;
    }
}
