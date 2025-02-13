package ru.yandex.practicum.commerce.warehouse.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.OTHER_ORDER_ID;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestOrderBookingWithDeliveryId;

@SpringBootTest
class OrderBookingRepositoryIT {

    @Autowired
    private OrderBookingRepository repository;

    @Test
    void whenFindByOrderIdAndOrderBookingNotExist_ThenReturnEmptyOptional() {

        final Optional<OrderBooking> orderBooking = repository.findByOrderId(OTHER_ORDER_ID);

        assertThat(orderBooking.isEmpty(), is(true));
    }

    @Test
    void whenFindByOrderIdAndOrderBookingExist_ThenReturnOptionalWithOrderBooking() {

        final Optional<OrderBooking> orderBooking = repository.findByOrderId(ORDER_ID);

        assertThat(orderBooking.isEmpty(), is(false));
        assertThat(orderBooking.get(), samePropertyValuesAs(getTestOrderBookingWithDeliveryId()));
    }
}