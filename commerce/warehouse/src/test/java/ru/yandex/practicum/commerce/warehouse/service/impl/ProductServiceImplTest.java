package ru.yandex.practicum.commerce.warehouse.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;
import ru.yandex.practicum.commerce.warehouse.util.LogListener;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_B;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddProductToWarehouseRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestBookedProducts;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductABooked;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductAIncreased;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductB;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductBBooked;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductBLow;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShoppingCart;
import static ru.yandex.practicum.commerce.warehouse.util.TestUtils.assertLogs;

class ProductServiceImplTest {

    private static final LogListener logListener = new LogListener(ProductServiceImpl.class);
    private AutoCloseable openMocks;
    private InOrder inOrder;

    @Mock
    private ProductRepository mockRepository;

    @Captor
    private ArgumentCaptor<Set<UUID>> uuidSetCaptor;

    @Captor
    private ArgumentCaptor<Collection<Product>> productsCaptor;

    private ProductService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        inOrder = Mockito.inOrder(mockRepository);
        logListener.startListen();
        logListener.reset();
        service = new ProductServiceImpl(mockRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository);
        openMocks.close();
    }

    @Test
    void whenAddNewProductAndProductExist_ThenThrowException() {
        when(mockRepository.existsById(any())).thenReturn(true);

        final SpecifiedProductAlreadyInWarehouseException exception = assertThrows(
                SpecifiedProductAlreadyInWarehouseException.class,
                () -> service.addNewProduct(getTestProductA())
        );

        verify(mockRepository).existsById(any());
        assertThat(exception.getUserMessage(), equalTo("Product with productId " + PRODUCT_ID_A + " already exists"));
    }

    @Test
    void whenAddNewProductAndProductNotExist_ThenPassItToRepositoryAndLog() throws Exception {
        when(mockRepository.existsById(any())).thenReturn(false);
        when(mockRepository.save(any())).thenReturn(getTestProductA());

        service.addNewProduct(getTestProductA());

        inOrder.verify(mockRepository).existsById(PRODUCT_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductA())));
        assertLogs(logListener.getEvents(), "add_new_product.json", getClass());
    }

    @Test
    void whenBookProductsInWarehouseAndProductNotExist_ThenThrowException() {
        when(mockRepository.findAllById(anySet())).thenReturn(List.of(getTestProductB()));

        final ProductInShoppingCartNotInWarehouse exception = assertThrows(ProductInShoppingCartNotInWarehouse.class,
                () -> service.bookProductsInWarehouse(getTestShoppingCart()));

        verify(mockRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Products not found in warehouse: " + PRODUCT_ID_A));
    }

    @Test
    void whenBookProductsInWarehouseAndProductNotSuffice_ThenThrowException() {
        when(mockRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductBLow()));

        final ProductInShoppingCartLowQuantityInWarehouse exception = assertThrows(
                ProductInShoppingCartLowQuantityInWarehouse.class,
                () -> service.bookProductsInWarehouse(getTestShoppingCart())
        );

        verify(mockRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Insufficient stocks in warehouse: " + PRODUCT_ID_B));
    }

    @Test
    void whenBookProductsInWarehouseAndProductSuffice_ThenUpdateBookedQuantityAndReturnDeliveryParametersAndLog()
            throws Exception {
        when(mockRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductB()));
        when(mockRepository.saveAll(anyCollection())).thenReturn(List.of(getTestProductABooked(),
                getTestProductBBooked()));

        final BookedProductsDto dto = service.bookProductsInWarehouse(getTestShoppingCart());

        inOrder.verify(mockRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        inOrder.verify(mockRepository).saveAll(productsCaptor.capture());
        assertThat(productsCaptor.getValue(), containsInAnyOrder(samePropertyValuesAs(getTestProductABooked()),
                samePropertyValuesAs(getTestProductBBooked())));
        assertThat(dto, equalTo(getTestBookedProducts()));
        assertLogs(logListener.getEvents(), "book_products.json", getClass());
    }

    @Test
    void whenIncreaseProductQuantityAndProductNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoSpecifiedProductInWarehouseException exception = assertThrows(
                NoSpecifiedProductInWarehouseException.class,
                () -> service.increaseProductQuantity(getTestAddProductToWarehouseRequest()));

        verify(mockRepository).findById(PRODUCT_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Product with productId " + PRODUCT_ID_A + " does not exist"));
    }

    @Test
    void whenIncreaseProductQuantityAndProductExist_ThenIncreaseProductQuantityAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestProductA()));
        when(mockRepository.save(any())).thenReturn(getTestProductAIncreased());

        service.increaseProductQuantity(getTestAddProductToWarehouseRequest());

        inOrder.verify(mockRepository).findById(PRODUCT_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductAIncreased())));
        assertLogs(logListener.getEvents(), "increase_product_quantity.json", getClass());
    }
}