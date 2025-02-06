package ru.yandex.practicum.commerce.delivery.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.delivery.model.Delivery;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.ORDER_ID_B;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDelivery;

@SpringBootTest
class DeliveryRepositoryIT {

    @Autowired
    private DeliveryRepository repository;

    @Test
    void whenExistsByOrderIdAndDeliveryExist_ThenReturnTrue() {

        final boolean deliveryExists = repository.existsByOrderId(ORDER_ID_A);

        assertThat(deliveryExists, is(true));
    }

    @Test
    void whenExistsByOrderIdAndDeliveryNotExist_ThenReturnFalse() {

        final boolean deliveryExists = repository.existsByOrderId(ORDER_ID_B);

        assertThat(deliveryExists, is(false));
    }

    @Test
    void whenFindByOrderIdAndDeliveryExist_ThenReturnOptionalWithDelivery() {

        final Optional<Delivery> delivery = repository.findByOrderId(ORDER_ID_A);

        assertThat(delivery.isEmpty(), is(false));
        assertThat(delivery.get(), samePropertyValuesAs(getTestDelivery()));
    }

    @Test
    void whenFindByOrderIdAndDeliveryNotExist_ThenReturnEmptyOptional() {

        final Optional<Delivery> delivery = repository.findByOrderId(ORDER_ID_B);

        assertThat(delivery.isEmpty(), is(true));
    }
}