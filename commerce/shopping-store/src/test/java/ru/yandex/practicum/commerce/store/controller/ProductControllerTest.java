package ru.yandex.practicum.commerce.store.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.service.ProductService;
import ru.yandex.practicum.commerce.store.util.LogListener;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.store.util.TestModels.CATEGORY_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.store.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestNewProduct;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestNewProductDto;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestPageable;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductB;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductDtoA;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductDtoB;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductDtoList;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductList;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestSetProductQuantityRequest;
import static ru.yandex.practicum.commerce.store.util.TestUtils.assertLogs;

class ProductControllerTest {

    private static final LogListener logListener = new LogListener(ProductController.class);
    private AutoCloseable openMocks;

    @Mock
    private ProductService mockService;

    @Mock
    private ProductMapper mockMapper;

    @Captor
    private ArgumentCaptor<List<Product>> productsCaptor;

    private InOrder inOrder;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new ProductController(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        openMocks.close();
    }

    @Test
    void whenAddProduct_ThenMapDtoToEntityAndPassToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockMapper.mapToEntity(any())).thenReturn(getTestNewProduct());
        when(mockService.addProduct(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        final ProductDto dto = controller.addProduct(getTestNewProductDto());

        inOrder.verify(mockMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockService).addProduct(refEq(getTestNewProduct()));
        inOrder.verify(mockMapper).mapToDto(refEq(getTestProductA()));
        assertThat(dto, equalTo(getTestProductDtoA()));
        assertLogs(logListener.getEvents(), "add_product.json", getClass());
    }

    @Test
    void whenGetProductById_ThenPassIdToServiceAndMapServiceResponseAndReturnIrAndLog() throws Exception {
        when(mockService.getProductById(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        final ProductDto dto = controller.getProductById(PRODUCT_ID_A);

        inOrder.verify(mockService).getProductById(PRODUCT_ID_A);
        inOrder.verify(mockMapper).mapToDto(refEq(getTestProductA()));
        assertThat(dto, equalTo(getTestProductDtoA()));
        assertLogs(logListener.getEvents(), "get_product.json", getClass());
    }

    @Test
    void whenFindProductsByCategory_ThenMapPageableAndPassWithCategoryToServiceAndMapServiceResponseAndReturnItAndLog()
        throws Exception {
        when(mockService.findProductsByCategory(any(), any())).thenReturn(getTestProductList());
        when(mockMapper.mapToDto(anyList())).thenReturn(getTestProductDtoList());

        final List<ProductDto> dtos = controller.findProductsByCategory(CATEGORY_A, getTestPageable());

        inOrder.verify(mockService).findProductsByCategory(CATEGORY_A, PAGEABLE);
        inOrder.verify(mockMapper).mapToDto(productsCaptor.capture());
        assertThat(productsCaptor.getValue(), contains(samePropertyValuesAs(getTestProductA()),
                samePropertyValuesAs(getTestProductB())));
        assertThat(dtos, contains(getTestProductDtoA(), getTestProductDtoB()));
        assertLogs(logListener.getEvents(), "get_products_by_category.json", getClass());
    }

    @Test
    void whenUpdateProduct_ThenMapDtoToEntityAndPassToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockMapper.mapToEntity(any())).thenReturn(getTestProductB());
        when(mockService.updateProduct(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        final ProductDto dto = controller.updateProduct(getTestProductDtoB());

        verify(mockMapper).mapToEntity(getTestProductDtoB());
        verify(mockService).updateProduct(refEq(getTestProductB()));
        verify(mockMapper).mapToDto(refEq(getTestProductA()));
        assertThat(dto, equalTo(getTestProductDtoA()));
        assertLogs(logListener.getEvents(), "update_product.json", getClass());
    }

    @Test
    void whenSetProductQuantity_ThenPassRequestToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.setProductQuantity(any())).thenReturn(true);

        final boolean result = controller.setProductQuantity(getTestSetProductQuantityRequest());

        verify(mockService).setProductQuantity(getTestSetProductQuantityRequest());
        assertThat(result, is(true));
        assertLogs(logListener.getEvents(), "update_quantity.json", getClass());
    }

    @Test
    void whenDeleteProduct_ThenPassProductIdToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.deleteProductById(any())).thenReturn(true);

        final boolean result = controller.deleteProduct(PRODUCT_ID_A);

        verify(mockService).deleteProductById(PRODUCT_ID_A);
        assertThat(result, is(true));
        assertLogs(logListener.getEvents(), "delete_product.json", getClass());
    }
}