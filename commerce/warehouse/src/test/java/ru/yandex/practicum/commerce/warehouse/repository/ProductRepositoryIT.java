package ru.yandex.practicum.commerce.warehouse.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.warehouse.model.Product;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;

@SpringBootTest
class ProductRepositoryIT {

    @Autowired
    private ProductRepository repository;

    @Test
    void whenFindByIdAndProductExist_ThenReturnOptionalWithProduct() {

        final Optional<Product> product = repository.findById(PRODUCT_ID_A);

        assertThat(product.isEmpty(), is(false));
        assertThat(product.get(), samePropertyValuesAs(getTestProductA()));
    }
}