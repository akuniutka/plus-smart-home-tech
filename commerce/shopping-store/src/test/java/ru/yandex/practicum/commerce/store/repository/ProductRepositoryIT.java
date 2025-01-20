package ru.yandex.practicum.commerce.store.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.store.model.Product;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static ru.yandex.practicum.commerce.store.util.TestModels.CATEGORY_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductA;

@SpringBootTest
class ProductRepositoryIT {

    @Autowired
    private ProductRepository repository;

    @Test
    void whenFindAllByProductCategory_ThenReturnCorrectProductList() {

        final List<Product> products = repository.findAllByProductCategory(CATEGORY_A, PAGEABLE);

        assertThat(products, contains(getTestProductA()));
    }
}