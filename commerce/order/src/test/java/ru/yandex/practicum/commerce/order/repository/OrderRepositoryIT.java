package ru.yandex.practicum.commerce.order.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderA;

@SpringBootTest
class OrderRepositoryIT {

    @Autowired
    private OrderRepository repository;

    @Test
    void whenFindAllByUsername_ThenReturnCorrectOrderList() {

        final List<Order> orders = repository.findAllByUsername(USERNAME_A, PAGEABLE);

        assertThat(orders, contains(samePropertyValuesAs(getTestOrderA())));
    }
}