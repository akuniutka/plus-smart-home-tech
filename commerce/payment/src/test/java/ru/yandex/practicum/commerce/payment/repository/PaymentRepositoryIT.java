package ru.yandex.practicum.commerce.payment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.payment.model.Payment;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.payment.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.payment.util.TestModels.ORDER_ID_B;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPayment;

@SpringBootTest
class PaymentRepositoryIT {

    @Autowired
    private PaymentRepository repository;

    @Test
    void whenExistsByOrderIdAndPaymentExist_ThenReturnTrue() {

        final boolean paymentExist = repository.existsByOrderId(ORDER_ID_A);

        assertThat(paymentExist, is(true));
    }

    @Test
    void whenExistsByOrderIdAndPaymentNoExist_ThenReturnFalse() {

        final boolean paymentExists = repository.existsByOrderId(ORDER_ID_B);

        assertThat(paymentExists, is(false));
    }

    @Test
    void whenFindByOrderIdAndPaymentExist_ThenReturnOptionalWithPayment() {

        final Optional<Payment> payment = repository.findByOrderId(ORDER_ID_A);

        assertThat(payment.isEmpty(), is(false));
        assertThat(payment.get(), samePropertyValuesAs(getTestPayment()));
    }

    @Test
    void whenFindByOrderIdAndPaymentNotExist_ThenReturnEmptyOptional() {

        final Optional<Payment> payment = repository.findByOrderId(ORDER_ID_B);

        assertThat(payment.isEmpty(), is(true));
    }
}