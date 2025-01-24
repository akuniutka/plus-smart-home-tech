package ru.yandex.practicum.commerce.cart.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.cart.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.cart.util.TestModels.USERNAME_B;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCart;

@SpringBootTest
class ShoppingCartRepositoryIT {

    @Autowired
    private ShoppingCartRepository repository;

    @Test
    void whenFindByUsernameAndCartExist_ThenReturnOptionalWithCart() {

        final Optional<ShoppingCart> shoppingCart = repository.findByUsername(USERNAME_A);

        assertThat(shoppingCart.isEmpty(), is(false));
        assertThat(shoppingCart.get(), samePropertyValuesAs(getTestFullShoppingCart()));
    }

    @Test
    void whenFindByUsernameAndCartNotExist_ThenReturnEmptyOptional() {

        final Optional<ShoppingCart> shoppingCart = repository.findByUsername(USERNAME_B);

        assertThat(shoppingCart.isEmpty(), is(true));
    }
}