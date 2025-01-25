package ru.yandex.practicum.commerce.warehouse.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.commerce.warehouse.model.Stock;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestStockA;

@SpringBootTest
class StockRepositoryIT {

    @Autowired
    private StockRepository repository;

    @Test
    void whenFindByIdAndStockExist_ThenReturnOptionalWithStock() {

        final Optional<Stock> stock = repository.findById(PRODUCT_ID_A);

        assertThat(stock.isEmpty(), is(false));
        assertThat(stock.get(), samePropertyValuesAs(getTestStockA()));
    }
}