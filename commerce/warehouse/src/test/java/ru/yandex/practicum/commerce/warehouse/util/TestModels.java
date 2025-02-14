package ru.yandex.practicum.commerce.warehouse.util;

import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.DimensionDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class TestModels {

    public static final int SCALE = 3;

    public static final UUID PRODUCT_ID_A = UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25");
    public static final boolean PRODUCT_FRAGILE_A = true;
    public static final BigDecimal PRODUCT_WIDTH_A = BigDecimal.valueOf(1000L, SCALE);
    public static final BigDecimal PRODUCT_HEIGHT_A = BigDecimal.valueOf(2000L, SCALE);
    public static final BigDecimal PRODUCT_DEPTH_A = BigDecimal.valueOf(3000L, SCALE);
    public static final BigDecimal PRODUCT_WEIGHT_A = BigDecimal.valueOf(4000L, SCALE);
    public static final long WAREHOUSE_PRODUCT_QUANTITY_A = 10L;

    public static final UUID PRODUCT_ID_B = UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808");
    public static final boolean PRODUCT_FRAGILE_B = false;
    public static final BigDecimal PRODUCT_WIDTH_B = BigDecimal.valueOf(2000L, SCALE);
    public static final BigDecimal PRODUCT_HEIGHT_B = BigDecimal.valueOf(3000L, SCALE);
    public static final BigDecimal PRODUCT_DEPTH_B = BigDecimal.valueOf(4000L, SCALE);
    public static final BigDecimal PRODUCT_WEIGHT_B = BigDecimal.valueOf(5000L, SCALE);
    public static final long WAREHOUSE_PRODUCT_QUANTITY_B = 20L;

    public static final UUID SHOPPING_CART_ID = UUID.fromString("801b5a89-c5f1-435c-a54e-d06cd6662a6a");
    public static final long SHOPPING_CART_PRODUCT_QUANTITY_A = 1L;
    public static final long SHOPPING_CART_PRODUCT_QUANTITY_B = 2L;

    public static final BigDecimal BOOKED_WEIGHT = BigDecimal.valueOf(14000, SCALE);
    public static final BigDecimal BOOKED_VOLUME = BigDecimal.valueOf(54000, SCALE);
    public static final boolean BOOKED_FRAGILE = true;

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    public static final UUID ORDER_BOOKING_ID = UUID.fromString("3c05ad1d-6d62-4af5-b3b7-ab0aefadceb4");
    public static final UUID ORDER_ID = UUID.fromString("f8991fc5-1c29-4395-b781-7717893cea92");
    public static final UUID OTHER_ORDER_ID = UUID.fromString("2b8dc08c-af0a-422f-b54b-eb2157e8bf59");
    public static final UUID DELIVERY_ID = UUID.fromString("1c44948b-78ca-48a5-80e5-6a901af8c117");

    private static final String ADDRESS_A = "ADDRESS_1";
    private static final String ADDRESS_B = "ADDRESS_2";

    private TestModels() {
        throw new AssertionError();
    }

    public static NewProductInWarehouseRequest getTestNewProductDto() {
        final DimensionDto dimension = new DimensionDto();
        dimension.setWidth(PRODUCT_WIDTH_A);
        dimension.setHeight(PRODUCT_HEIGHT_A);
        dimension.setDepth(PRODUCT_DEPTH_A);
        final NewProductInWarehouseRequest dto = new NewProductInWarehouseRequest();
        dto.setProductId(PRODUCT_ID_A);
        dto.setFragile(PRODUCT_FRAGILE_A);
        dto.setDimension(dimension);
        dto.setWeight(PRODUCT_WEIGHT_A);
        return dto;
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
        product.setQuantity(WAREHOUSE_PRODUCT_QUANTITY_A);
        return product;
    }

    public static Product getTestProductAIncreased() {
        final Product product = getTestProductA();
        product.setQuantity(product.getQuantity() + SHOPPING_CART_PRODUCT_QUANTITY_A);
        return product;
    }

    public static Product getTestProductADecreased() {
        final Product product = getTestProductA();
        product.setQuantity(product.getQuantity() - SHOPPING_CART_PRODUCT_QUANTITY_A);
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
        product.setQuantity(WAREHOUSE_PRODUCT_QUANTITY_B);
        return product;
    }

    public static Product getTestProductBLow() {
        final Product product = getTestProductB();
        product.setQuantity(1L);
        return product;
    }

    public static Product getTestProductBDecreased() {
        final Product product = getTestProductB();
        product.setQuantity(product.getQuantity() - SHOPPING_CART_PRODUCT_QUANTITY_B);
        return product;
    }

    public static ShoppingCartDto getTestShoppingCart() {
        final ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(SHOPPING_CART_ID);
        dto.setProducts(Map.of(
                PRODUCT_ID_A, SHOPPING_CART_PRODUCT_QUANTITY_A,
                PRODUCT_ID_B, SHOPPING_CART_PRODUCT_QUANTITY_B
        ));
        return dto;
    }

    public static DeliveryParams getTestDeliveryParams() {
        return new DeliveryParams(BOOKED_WEIGHT, BOOKED_VOLUME, BOOKED_FRAGILE);
    }

    public static BookedProductsDto getTestBookedProducts() {
        final BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(BOOKED_WEIGHT);
        dto.setDeliveryVolume(BOOKED_VOLUME);
        dto.setFragile(BOOKED_FRAGILE);
        return dto;
    }

    public static AddProductToWarehouseRequest getTestAddProductToWarehouseRequest() {
        final AddProductToWarehouseRequest request = new AddProductToWarehouseRequest();
        request.setProductId(PRODUCT_ID_A);
        request.setQuantity(SHOPPING_CART_PRODUCT_QUANTITY_A);
        return request;
    }

    public static AddressDto getTestAddressDtoA() {
        return getTestAddress(ADDRESS_A);
    }

    public static AddressDto getTestAddressDtoB() {
        return getTestAddress(ADDRESS_B);
    }

    public static AssemblyProductsForOrderRequest getTestAssemblyProductsForOrderRequest() {
        final AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
        request.setProducts(Map.of(
                PRODUCT_ID_A, SHOPPING_CART_PRODUCT_QUANTITY_A,
                PRODUCT_ID_B, SHOPPING_CART_PRODUCT_QUANTITY_B
        ));
        request.setOrderId(ORDER_ID);
        return request;
    }

    public static OrderBooking getTestOrderBookingNew() {
        final OrderBooking orderBooking = new OrderBooking();
        orderBooking.setOrderBookingId(ORDER_BOOKING_ID);
        orderBooking.setProducts(Map.of(
                PRODUCT_ID_A, SHOPPING_CART_PRODUCT_QUANTITY_A,
                PRODUCT_ID_B, SHOPPING_CART_PRODUCT_QUANTITY_B
        ));
        orderBooking.setOrderId(ORDER_ID);
        return orderBooking;
    }

    public static OrderBooking getTestOrderBookingWithDeliveryId() {
        final OrderBooking orderBooking = getTestOrderBookingNew();
        orderBooking.setDeliveryId(DELIVERY_ID);
        return orderBooking;
    }

    public static ShippedToDeliveryRequest getTestShippedToDeliveryRequest() {
        final ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(ORDER_ID);
        request.setDeliveryId(DELIVERY_ID);
        return request;
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
