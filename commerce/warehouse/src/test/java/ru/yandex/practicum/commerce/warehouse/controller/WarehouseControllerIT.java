package ru.yandex.practicum.commerce.warehouse.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddProductToWarehouseRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestBookedProducts;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestNewProductDto;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShoppingCart;
import static ru.yandex.practicum.commerce.warehouse.util.TestUtils.loadJson;

@WebMvcTest(controllers = WarehouseController.class)
class WarehouseControllerIT {

    private static final String BASE_PATH = "/api/v1/warehouse";
    private InOrder inOrder;

    @MockBean
    private ProductService mockProductService;

    @MockBean
    private AddressService mockAddressService;

    @MockBean
    private ProductMapper mockProductMapper;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProductService, mockAddressService, mockAddressService);
        inOrder = Mockito.inOrder(mockProductService, mockAddressService, mockProductMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockProductService, mockAddressService, mockProductMapper);
    }

    @Test
    void whenPutAtBasePath_ThenInvokeAddNewProductMethod() throws Exception {
        final String requestBody = loadJson("add_new_product_request.json", getClass());
        when(mockProductMapper.mapToEntity(any())).thenReturn(getTestProductA());
        doNothing().when(mockProductService).addNewProduct(any());

        mvc.perform(put(BASE_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(
                        status().isOk());

        inOrder.verify(mockProductMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockProductService).addNewProduct(refEq(getTestProductA()));
    }

    @Test
    void whenPostAtCheckEndpoint_ThenInvokeBookProductsMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("book_products_request.json", getClass());
        final String responseBody = loadJson("book_products_response.json", getClass());
        when(mockProductService.bookProductsInWarehouse(any())).thenReturn(getTestBookedProducts());

        mvc.perform(post(BASE_PATH + "/check")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockProductService).bookProductsInWarehouse(getTestShoppingCart());
    }

    @Test
    void whenPostAtAddEndpoint_ThenInvokeIncreaseProductQuantity() throws Exception {
        final String requestBody = loadJson("add_product_quantity.json", getClass());
        doNothing().when(mockProductService).increaseProductQuantity(any());

        mvc.perform(post(BASE_PATH + "/add")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockProductService).increaseProductQuantity(getTestAddProductToWarehouseRequest());
    }

    @Test
    void whenGetAtAddressEndpoint_ThenInvokerGetWarehouseAddressAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_address_request.json", getClass());
        when(mockAddressService.getAddress()).thenReturn(getTestAddressDtoA());

        mvc.perform(get(BASE_PATH + "/address")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockAddressService).getAddress();
    }

    @Test
    void whenSpecifiedProductAlreadyInWarehouseException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("add_new_product_request.json", getClass());
        when(mockProductMapper.mapToEntity(any())).thenReturn(getTestProductA());
        doThrow(new SpecifiedProductAlreadyInWarehouseException(TEST_EXCEPTION_MESSAGE))
                .when(mockProductService).addNewProduct(any());

        mvc.perform(put(BASE_PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockProductMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockProductService).addNewProduct(refEq(getTestProductA()));
    }

    @Test
    void whenProductInShoppingCartNotInWarehouse_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("book_products_request.json", getClass());
        when(mockProductService.bookProductsInWarehouse(any()))
                .thenThrow(new ProductInShoppingCartNotInWarehouse(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/check")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).bookProductsInWarehouse(getTestShoppingCart());
    }

    @Test
    void whenProductInShoppingCartLowQuantityInWarehouse_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("book_products_request.json", getClass());
        when(mockProductService.bookProductsInWarehouse(any()))
                .thenThrow(new ProductInShoppingCartLowQuantityInWarehouse(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/check")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).bookProductsInWarehouse(getTestShoppingCart());
    }

    @Test
    void whenNoSpecifiedProductInWarehouseException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("add_product_quantity.json", getClass());
        doThrow(new NoSpecifiedProductInWarehouseException(TEST_EXCEPTION_MESSAGE))
                .when(mockProductService).increaseProductQuantity(any());

        mvc.perform(post(BASE_PATH + "/add")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).increaseProductQuantity(getTestAddProductToWarehouseRequest());
    }
}