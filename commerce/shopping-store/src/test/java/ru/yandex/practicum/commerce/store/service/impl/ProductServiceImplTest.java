package ru.yandex.practicum.commerce.store.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;
import ru.yandex.practicum.commerce.store.service.ProductService;
import ru.yandex.practicum.commerce.store.util.LogListener;
import ru.yandex.practicum.commerce.store.util.UUIDGenerator;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.store.util.TestModels.CATEGORY_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.store.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.PRODUCT_ID_B;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestNewProduct;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductADeactivated;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductAWithUpdatedQuantity;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductB;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductList;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestSetProductQuantityRequest;
import static ru.yandex.practicum.commerce.store.util.TestUtils.assertLogs;

class ProductServiceImplTest {

    private static final LogListener logListener = new LogListener(ProductServiceImpl.class);
    private ProductRepository mockRepository;
    private UUIDGenerator mockUUIDGenerator;

    private ProductService service;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(ProductRepository.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        logListener.startListen();
        logListener.reset();
        service = new ProductServiceImpl(mockRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, mockUUIDGenerator);
    }

    @Test
    void whenAddProduct_ThenAssignProductIdAndSaveInRepositoryAndReturnSavedAndLog() throws Exception {
        when(mockUUIDGenerator.getNewUUID()).thenReturn(PRODUCT_ID_A);
        when(mockRepository.save(any())).thenReturn(getTestProductA());

        final Product product = service.addProduct(getTestNewProduct());

        verify(mockUUIDGenerator).getNewUUID();
        verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductA())));
        assertThat(product, samePropertyValuesAs(getTestProductA()));
        assertLogs(logListener.getEvents(), "add_product.json", getClass());
    }

    @Test
    void whenGetProductByIdAndProductExist_ThenReturnProduct() {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestProductA()));

        final Product product = service.getProductById(PRODUCT_ID_A);

        verify(mockRepository).findById(PRODUCT_ID_A);
        assertThat(product, samePropertyValuesAs(getTestProductA()));
    }

    @Test
    void whenGetProductByIdAndProductNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> service.getProductById(PRODUCT_ID_A));

        verify(mockRepository).findById(PRODUCT_ID_A);
        assertThat(exception.getMessage(), equalTo("Product not found: productId = " + PRODUCT_ID_A));
    }

    @Test
    void whenFindProductsByCategory_ThenReturnListOfProducts() {
        when(mockRepository.findAllByProductCategory(any(), any())).thenReturn(getTestProductList());

        final List<Product> products = service.findProductsByCategory(CATEGORY_A, PAGEABLE);

        verify(mockRepository).findAllByProductCategory(CATEGORY_A, PAGEABLE);
        assertThat(products, contains(samePropertyValuesAs(getTestProductA()),
                samePropertyValuesAs(getTestProductB())));
    }

    @Test
    void whenUpdateProductAndProductExist_ThenSaveInRepositoryAndReturnSavedAndLog() throws Exception {
        when(mockRepository.existsById(any())).thenReturn(true);
        when(mockRepository.save(any())).thenReturn(getTestProductA());

        final Product product = service.updateProduct(getTestProductB());

        verify(mockRepository).existsById(PRODUCT_ID_B);
        verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductB())));
        assertThat(product, samePropertyValuesAs(getTestProductA()));
        assertLogs(logListener.getEvents(), "update_product.json", getClass());
    }

    @Test
    void whenUpdateProductAndProductNotExist_WhenThrowException() {
        when(mockRepository.existsById(any())).thenReturn(false);

        final ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> service.updateProduct(getTestProductA()));

        verify(mockRepository).existsById(PRODUCT_ID_A);
        assertThat(exception.getMessage(), equalTo("Product not found: productId = " + PRODUCT_ID_A));
    }

    @Test
    void whenSetProductQuantityAndProductExist_ThenUpdateQuantityAndReturnTrueAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestProductA()));
        when(mockRepository.save(any())).thenReturn(getTestProductAWithUpdatedQuantity());

        final boolean result = service.setProductQuantity(getTestSetProductQuantityRequest());

        verify(mockRepository).findById(PRODUCT_ID_A);
        verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductAWithUpdatedQuantity())));
        assertThat(result, is(true));
        assertLogs(logListener.getEvents(), "update_quantity.json", getClass());
    }

    @Test
    void whenSetProductQuantityAndProductNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> service.setProductQuantity(getTestSetProductQuantityRequest()));

        verify(mockRepository).findById(PRODUCT_ID_A);
        assertThat(exception.getMessage(), equalTo("Product not found: productId = " + PRODUCT_ID_A));
    }

    @Test
    void whenDeleteProductByIdAndProductExist_ThenUpdateProductStateAndReturnTrueAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestProductA()));
        when(mockRepository.save(any())).thenReturn(getTestProductADeactivated());

        final boolean result = service.deleteProductById(PRODUCT_ID_A);

        verify(mockRepository).findById(PRODUCT_ID_A);
        verify(mockRepository).save(argThat(samePropertyValuesAs(getTestProductADeactivated())));
        assertThat(result, is(true));
        assertLogs(logListener.getEvents(), "delete_product.json", getClass());
    }

    @Test
    void whenDeleteProductByIdAndProductNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> service.deleteProductById(PRODUCT_ID_A));

        verify(mockRepository).findById(PRODUCT_ID_A);
        assertThat(exception.getMessage(), equalTo("Product not found: productId = " + PRODUCT_ID_A));
    }
}