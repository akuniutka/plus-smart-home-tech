package ru.yandex.practicum.commerce.delivery.util;

import ru.yandex.practicum.commerce.delivery.model.Address;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class TestModels {

    public static final UUID DELIVERY_ID = UUID.fromString("1c44948b-78ca-48a5-80e5-6a901af8c117");
    public static final UUID ORDER_ID_A = UUID.fromString("f8991fc5-1c29-4395-b781-7717893cea92");
    public static final UUID ORDER_ID_B = UUID.fromString("2b8dc08c-af0a-422f-b54b-eb2157e8bf59");
    public static final int COST_SCALE = 2;
    public static final BigDecimal DELIVERY_COST = BigDecimal.valueOf(3240L, COST_SCALE);
    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    private TestModels() {
        throw new AssertionError();
    }

    public static Delivery getTestNewDelivery() {
        final Delivery delivery = new Delivery();
        delivery.setFromAddress(getTestFromAddress());
        delivery.setToAddress(getTestToAddress());
        delivery.setOrderId(ORDER_ID_A);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return delivery;
    }

    public static Delivery getTestDelivery() {
        final Delivery delivery = getTestNewDelivery();
        delivery.setDeliveryId(DELIVERY_ID);
        return delivery;
    }

    public static Delivery getTestDeliveryPicked() {
        final Delivery delivery = getTestDelivery();
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        return delivery;
    }

    public static Delivery getTestDeliveryConfirmed() {
        final Delivery delivery = getTestDelivery();
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        return delivery;
    }

    public static Delivery getTestDeliveryFailed() {
        final Delivery delivery = getTestDelivery();
        delivery.setDeliveryState(DeliveryState.FAILED);
        return delivery;
    }

    public static DeliveryDto getTestNewDeliveryDto() {
        final DeliveryDto dto = new DeliveryDto();
        dto.setFromAddress(getTestFromAddressDto());
        dto.setToAddress(getTestToAddressDto());
        dto.setOrderId(ORDER_ID_A);
        dto.setDeliveryState(DeliveryState.CREATED);
        return dto;
    }

    public static DeliveryDto getTestDeliveryDto() {
        final DeliveryDto dto = getTestNewDeliveryDto();
        dto.setDeliveryId(DELIVERY_ID);
        return dto;
    }

    public static ShippedToDeliveryRequest getTestShippedToDeliveryRequest() {
        final ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(ORDER_ID_A);
        request.setDeliveryId(DELIVERY_ID);
        return request;
    }

    public static OrderDto getTestOrderDto() {
        final OrderDto dto = new OrderDto();
        dto.setOrderId(ORDER_ID_A);
        dto.setShoppingCartId(UUID.fromString("3771b0ec-41ec-4c28-80e1-d28b7092df8c"));
        dto.setProducts(getTestProducts());
        dto.setDeliveryId(DELIVERY_ID);
        dto.setState(OrderState.NEW);
        dto.setDeliveryWeight(BigDecimal.valueOf(14000L, 3));
        dto.setDeliveryVolume(BigDecimal.valueOf(54000L, 3));
        dto.setFragile(true);
        return dto;
    }

    public static OrderDto getTestOrderDtoAssembled() {
        final OrderDto dto = getTestOrderDto();
        dto.setPaymentId(UUID.fromString("ff035258-939e-4f09-8119-61459eb68949"));
        dto.setState(OrderState.ASSEMBLED);
        dto.setTotalPrice(BigDecimal.valueOf(2598L, 2));
        dto.setDeliveryPrice(BigDecimal.valueOf(599L, 2));
        dto.setProductPrice(BigDecimal.valueOf(1999L, 2));
        return dto;
    }

    public static OrderDto getTestOrderDtoDelivered() {
        final OrderDto dto = getTestOrderDtoAssembled();
        dto.setState(OrderState.DELIVERED);
        return dto;
    }

    public static OrderDto getTestOrderDtoNotDelivered() {
        final OrderDto dto = getTestOrderDtoAssembled();
        dto.setState(OrderState.DELIVERY_FAILED);
        return dto;
    }

    private static Address getTestFromAddress() {
        final Address address = new Address();
        address.setCountry("ADDRESS_1");
        address.setCity("ADDRESS_1");
        address.setStreet("ADDRESS_1");
        address.setHouse("ADDRESS_1");
        address.setFlat("ADDRESS_1");
        return address;
    }

    private static Address getTestToAddress() {
        final Address address = new Address();
        address.setCountry("NOLAND");
        address.setCity("STONE CITY");
        address.setStreet("FIRST LANE");
        address.setHouse("15");
        address.setFlat("42");
        return address;
    }

    private static AddressDto getTestFromAddressDto() {
        final AddressDto dto = new AddressDto();
        dto.setCountry("ADDRESS_1");
        dto.setCity("ADDRESS_1");
        dto.setStreet("ADDRESS_1");
        dto.setHouse("ADDRESS_1");
        dto.setFlat("ADDRESS_1");
        return dto;
    }

    private static AddressDto getTestToAddressDto() {
        final AddressDto dto = new AddressDto();
        dto.setCountry("NOLAND");
        dto.setCity("STONE CITY");
        dto.setStreet("FIRST LANE");
        dto.setHouse("15");
        dto.setFlat("42");
        return dto;
    }

    private static Map<UUID, Long> getTestProducts() {
        return Map.of(
                UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25"), 1L,
                UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808"), 2L
        );
    }
}
