package ru.yandex.practicum.commerce.order.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.dto.store.Pageable;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.order.model.Address;
import ru.yandex.practicum.commerce.order.model.Order;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class TestModels {

    public static final String USERNAME_A = "alice";
    public static final String WRONG_USERNAME = "";

    public static final UUID ORDER_ID_A = UUID.fromString("f8991fc5-1c29-4395-b781-7717893cea92");
    public static final UUID SHOPPING_CART_ID_A = UUID.fromString("801b5a89-c5f1-435c-a54e-d06cd6662a6a");

    public static final UUID PAYMENT_ID_A = UUID.fromString("ff035258-939e-4f09-8119-61459eb68949");
    public static final BigDecimal TOTAL_PRICE_A = BigDecimal.valueOf(2598L, 2);
    public static final BigDecimal DELIVERY_PRICE_A = BigDecimal.valueOf(599L, 2);
    public static final BigDecimal PRODUCT_PRICE_A = BigDecimal.valueOf(1999L, 2);

    public static final UUID DELIVERY_ID_A = UUID.fromString("1c44948b-78ca-48a5-80e5-6a901af8c117");
    public static final BigDecimal DELIVERY_WEIGHT_A = BigDecimal.valueOf(14000L, 3);
    public static final BigDecimal DELIVERY_VOLUME_A = BigDecimal.valueOf(54000L, 3);
    public static final boolean DELIVERY_FRAGILE_A = true;

    public static final String DELIVERY_COUNTRY_A = "NOLAND";
    public static final String DELIVERY_CITY_A = "STONE CITY";
    public static final String DELIVERY_STREET_A = "FIRST LANE";
    public static final String DELIVERY_HOUSE_A = "15";
    public static final String DELIVERY_FLAT_A = "42";

    public static final UUID ORDER_ID_B = UUID.fromString("2b8dc08c-af0a-422f-b54b-eb2157e8bf59");
    public static final UUID SHOPPING_CART_ID_B = UUID.fromString("3771b0ec-41ec-4c28-80e1-d28b7092df8c");

    public static final UUID PAYMENT_ID_B = UUID.fromString("6b32d033-a160-4193-9f22-a2714c512735");
    public static final BigDecimal TOTAL_PRICE_B = BigDecimal.valueOf(7199L, 2);
    public static final BigDecimal DELIVERY_PRICE_B = BigDecimal.valueOf(1200L, 2);
    public static final BigDecimal PRODUCT_PRICE_B = BigDecimal.valueOf(5999L, 2);

    public static final UUID DELIVERY_ID_B = UUID.fromString("83b2e862-2613-4b3e-a910-b24afafd6983");
    public static final BigDecimal DELIVERY_WEIGHT_B = BigDecimal.valueOf(65500L, 3);
    public static final BigDecimal DELIVERY_VOLUME_B = BigDecimal.valueOf(33333L, 3);
    public static final boolean DELIVERY_FRAGILE_B = false;

    public static final String DELIVERY_COUNTRY_B = "NOLAND";
    public static final String DELIVERY_CITY_B = "STONE CITY";
    public static final String DELIVERY_STREET_B = "FIRST LANE";
    public static final String DELIVERY_HOUSE_B = "11";
    public static final String DELIVERY_FLAT_B = "2";

    public static final String[] SORT_BY = {"state", "totalPrice"};
    public static final org.springframework.data.domain.Pageable PAGEABLE = PageRequest.of(1, 2, Sort.by(SORT_BY));

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    private TestModels() {
        throw new AssertionError();
    }

    public static CreateNewOrderRequest getTestCreateNewOrderRequest() {
        final CreateNewOrderRequest request = new CreateNewOrderRequest();
        request.setShoppingCart(getTestShoppingCartA());
        request.setDeliveryAddress(getTestAddressDtoA());
        return request;
    }

    public static ShoppingCartDto getTestShoppingCartA() {
        final ShoppingCartDto shoppingCart = new ShoppingCartDto();
        shoppingCart.setShoppingCartId(SHOPPING_CART_ID_A);
        shoppingCart.setProducts(getTestProductsA());
        return shoppingCart;
    }

    public static AddressDto getTestAddressDtoA() {
        final AddressDto dto = new AddressDto();
        dto.setCountry(DELIVERY_COUNTRY_A);
        dto.setCity(DELIVERY_CITY_A);
        dto.setStreet(DELIVERY_STREET_A);
        dto.setHouse(DELIVERY_HOUSE_A);
        dto.setFlat(DELIVERY_FLAT_A);
        return dto;
    }

    public static Address getTestAddressA() {
        final Address address = new Address();
        address.setCountry(DELIVERY_COUNTRY_A);
        address.setCity(DELIVERY_CITY_A);
        address.setStreet(DELIVERY_STREET_A);
        address.setHouse(DELIVERY_HOUSE_A);
        address.setFlat(DELIVERY_FLAT_A);
        return address;
    }

    public static Address getTestAddressB() {
        final Address address = new Address();
        address.setCountry(DELIVERY_COUNTRY_B);
        address.setCity(DELIVERY_CITY_B);
        address.setStreet(DELIVERY_STREET_B);
        address.setHouse(DELIVERY_HOUSE_B);
        address.setFlat(DELIVERY_FLAT_B);
        return address;
    }

    public static Pageable getTestPageable() {
        final Pageable pageable = new Pageable();
        pageable.setPage(PAGEABLE.getPageNumber());
        pageable.setSize(PAGEABLE.getPageSize());
        pageable.setSort(SORT_BY);
        return pageable;
    }

    public static BookedProductsDto getTestBookedProductsDto() {
        final BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(DELIVERY_WEIGHT_A);
        dto.setDeliveryVolume(DELIVERY_VOLUME_A);
        dto.setFragile(DELIVERY_FRAGILE_A);
        return dto;
    }

    public static Order getTestNewOrder() {
        final Order order = new Order();
        order.setOrderId(ORDER_ID_A);
        order.setUsername(USERNAME_A);
        order.setShoppingCartId(SHOPPING_CART_ID_A);
        order.setProducts(getTestProductsA());
        order.setState(OrderState.NEW);
        order.setDeliveryAddress(getTestAddressA());
        return order;
    }

    public static Order getTestOrderA() {
        final Order order = getTestNewOrder();
        order.setPaymentId(PAYMENT_ID_A);
        order.setDeliveryId(DELIVERY_ID_A);
        order.setState(OrderState.COMPLETED);
        order.setDeliveryWeight(DELIVERY_WEIGHT_A);
        order.setDeliveryVolume(DELIVERY_VOLUME_A);
        order.setFragile(DELIVERY_FRAGILE_A);
        order.setTotalPrice(TOTAL_PRICE_A);
        order.setDeliveryPrice(DELIVERY_PRICE_A);
        order.setProductPrice(PRODUCT_PRICE_A);
        return order;
    }

    public static Order getTestOrderAAssembled() {
        final Order order = getTestOrderA();
        order.setState(OrderState.ASSEMBLED);
        return order;
    }

    public static Order getTestOrderAPaid() {
        final Order order = getTestOrderA();
        order.setState(OrderState.PAID);
        return order;
    }

    public static Order getTestOrderAUnpaid() {
        final Order order = getTestOrderA();
        order.setState(OrderState.PAYMENT_FAILED);
        return order;
    }

    public static Order getTestOrderB() {
        final Order order = new Order();
        order.setOrderId(ORDER_ID_B);
        order.setUsername(USERNAME_A);
        order.setShoppingCartId(SHOPPING_CART_ID_B);
        order.setProducts(getTestProductsB());
        order.setPaymentId(PAYMENT_ID_B);
        order.setDeliveryId(DELIVERY_ID_B);
        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryAddress(getTestAddressB());
        order.setDeliveryWeight(DELIVERY_WEIGHT_B);
        order.setDeliveryVolume(DELIVERY_VOLUME_B);
        order.setFragile(DELIVERY_FRAGILE_B);
        order.setTotalPrice(TOTAL_PRICE_B);
        order.setDeliveryPrice(DELIVERY_PRICE_B);
        order.setProductPrice(PRODUCT_PRICE_B);
        return order;
    }

    public static OrderDto getTestNewOrderDto() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID_A);
        dto.setShoppingCartId(SHOPPING_CART_ID_A);
        dto.setProducts(getTestProductsA());
        dto.setState(OrderState.NEW);
        return dto;
    }

    public static OrderDto getTestOrderDtoA() {
        final OrderDto dto = getTestNewOrderDto();
        dto.setPaymentId(PAYMENT_ID_A);
        dto.setDeliveryId(DELIVERY_ID_A);
        dto.setState(OrderState.COMPLETED);
        dto.setDeliveryWeight(DELIVERY_WEIGHT_A);
        dto.setDeliveryVolume(DELIVERY_VOLUME_A);
        dto.setFragile(DELIVERY_FRAGILE_A);
        dto.setTotalPrice(TOTAL_PRICE_A);
        dto.setDeliveryPrice(DELIVERY_PRICE_A);
        dto.setProductPrice(PRODUCT_PRICE_A);
        return dto;
    }

    public static OrderDto getTestOrderDtoAPaid() {
        final OrderDto dto = getTestOrderDtoA();
        dto.setState(OrderState.PAID);
        return dto;
    }

    public static OrderDto getTestOrderDtoAUnpaid() {
        final OrderDto dto = getTestOrderDtoA();
        dto.setState(OrderState.PAYMENT_FAILED);
        return dto;
    }

    public static OrderDto getTestOrderDtoB() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID_B);
        dto.setShoppingCartId(SHOPPING_CART_ID_B);
        dto.setProducts(getTestProductsB());
        dto.setPaymentId(PAYMENT_ID_B);
        dto.setDeliveryId(DELIVERY_ID_B);
        dto.setState(OrderState.ASSEMBLED);
        dto.setDeliveryWeight(DELIVERY_WEIGHT_B);
        dto.setDeliveryVolume(DELIVERY_VOLUME_B);
        dto.setFragile(DELIVERY_FRAGILE_B);
        dto.setTotalPrice(TOTAL_PRICE_B);
        dto.setDeliveryPrice(DELIVERY_PRICE_B);
        dto.setProductPrice(PRODUCT_PRICE_B);
        return dto;
    }

    private static Map<UUID, Long> getTestProductsA() {
        return Map.of(
                UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25"), 1L,
                UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808"), 2L
        );
    }

    private static Map<UUID, Long> getTestProductsB() {
        return Map.of(
                UUID.fromString("c504066c-e949-46c6-b6b3-31a9bdcb3176"), 7L,
                UUID.fromString("dbe1ac64-08b7-4209-a877-21caa44ede79"), 8L
        );
    }
}
