package ru.yandex.practicum.commerce.store.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.commerce.exception.ApiExceptions;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.service.ProductService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.store.util.TestModels.CATEGORY_A;
import static ru.yandex.practicum.commerce.store.util.TestModels.EXCEPTION_MESSAGE;
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
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestProductNotFoundException;
import static ru.yandex.practicum.commerce.store.util.TestModels.getTestSetProductQuantityRequest;
import static ru.yandex.practicum.commerce.store.util.TestUtils.loadJson;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerIT {

    private static final String BASE_PATH = "/api/v1/shopping-store";
    private InOrder inOrder;

    @MockBean
    private ProductService mockService;

    @MockBean
    private ProductMapper mockMapper;

    @Captor
    private ArgumentCaptor<List<Product>> productsCaptor;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockService, mockMapper);
        inOrder = Mockito.inOrder(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void whenPostAtBasePath_ThenInvokeAddProductMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("add_product_request.json", getClass());
        final String responseBody = loadJson("add_product_response.json", getClass());
        when(mockMapper.mapToEntity(any())).thenReturn(getTestNewProduct());
        when(mockService.addProduct(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        mvc.perform(post(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockService).addProduct(refEq(getTestNewProduct()));
        inOrder.verify(mockMapper).mapToDto(refEq(getTestProductA()));
    }

    @Test
    void whenGetAtBasePathWithProductId_ThenInvokeGetProductByIdMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_product.json", getClass());
        when(mockService.getProductById(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        mvc.perform(get(BASE_PATH + "/" + PRODUCT_ID_A)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getProductById(PRODUCT_ID_A);
        inOrder.verify(mockMapper).mapToDto(refEq(getTestProductA()));
    }

    @Test
    void whenGetAtBasePath_ThenInvokeGetProductsByCategoryMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_products_by_category_response.json", getClass());
        when(mockService.findProductsByCategory(any(), any())).thenReturn(getTestProductList());
        when(mockMapper.mapToDto(anyList())).thenReturn(getTestProductDtoList());

        mvc.perform(get(BASE_PATH)
                        .param("category", CATEGORY_A.name())
                        .param("page", String.valueOf(getTestPageable().getPage()))
                        .param("size", String.valueOf(getTestPageable().getSize()))
                        .param("sort", getTestPageable().getSort())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).findProductsByCategory(CATEGORY_A, PAGEABLE);
        inOrder.verify(mockMapper).mapToDto(productsCaptor.capture());
        assertThat(productsCaptor.getValue(), contains(samePropertyValuesAs(getTestProductA()),
                samePropertyValuesAs(getTestProductB())));
    }

    @Test
    void whenPutAtBasePath_ThenInvokeUpdateProductMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("update_product_request.json", getClass());
        final String responseBody = loadJson("update_product_response.json", getClass());
        when(mockMapper.mapToEntity(any())).thenReturn(getTestProductB());
        when(mockService.updateProduct(any())).thenReturn(getTestProductA());
        when(mockMapper.mapToDto(any(Product.class))).thenReturn(getTestProductDtoA());

        mvc.perform(put(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockMapper).mapToEntity(getTestProductDtoB());
        inOrder.verify(mockService).updateProduct(refEq(getTestProductB()));
        inOrder.verify(mockMapper).mapToDto(refEq(getTestProductA()));
    }

    @Test
    void whenPostAtQuantityStateEndpoint_ThenInvokeSetProductQuantityMethodAndProcessResponse() throws Exception {
        final String responseBody = "true";
        when(mockService.setProductQuantity(any())).thenReturn(true);

        mvc.perform(post(BASE_PATH + "/quantityState")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productId", getTestSetProductQuantityRequest().getProductId().toString())
                        .param("quantityState", getTestSetProductQuantityRequest().getQuantityState().name()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(responseBody));

        inOrder.verify(mockService).setProductQuantity(getTestSetProductQuantityRequest());
    }

    @Test
    void whenPostAtRemoveProductFromStoreEndpoint_ThenInvokeDeleteProductMethodAndProcessResponse() throws Exception {
        final String requestBode = "\"%s\"".formatted(PRODUCT_ID_A);
        final String responseBody = "true";
        when(mockService.deleteProductById(any())).thenReturn(true);

        mvc.perform(post(BASE_PATH + "/removeProductFromStore")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBode))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(responseBody));

        inOrder.verify(mockService).deleteProductById(PRODUCT_ID_A);
    }

    @Test
    void whenProductNotFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        when(mockService.getProductById(any())).thenThrow(getTestProductNotFoundException());

        mvc.perform(get(BASE_PATH + "/" + PRODUCT_ID_A)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                ProductNotFoundException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.userMessage", equalTo(EXCEPTION_MESSAGE)));

        inOrder.verify(mockService).getProductById(PRODUCT_ID_A);
    }
}