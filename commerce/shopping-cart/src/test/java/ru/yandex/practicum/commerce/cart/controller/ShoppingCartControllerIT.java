package ru.yandex.practicum.commerce.cart.controller;

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
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.exception.ApiExceptions;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.ShoppingCartDeactivatedException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.cart.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.cart.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.cart.util.TestModels.WRONG_USERNAME;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestBookedProductsDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestChangeProductQuantityRequest;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCartDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToAdd;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToDelete;
import static ru.yandex.practicum.commerce.cart.util.TestUtils.loadJson;

@WebMvcTest(controllers = ShoppingCartController.class)
class ShoppingCartControllerIT {

    private static final String BASE_PATH = "/api/v1/shopping-cart";
    private InOrder inOrder;

    @MockBean
    private ShoppingCartService mockService;

    @MockBean
    private ShoppingCartMapper mockMapper;

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
    void whenGetAtBasePath_ThenInvokeGetShoppingCartByUsernameMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_shopping_cart_response.json", getClass());
        when(mockService.getShoppingCartByUsername(any())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        mvc.perform(get(BASE_PATH)
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).getShoppingCartByUsername(USERNAME_A);
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
    }

    @Test
    void whenPutAtBasePath_ThenInvokeAddProductsToShoppingCartMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("add_products_request.json", getClass());
        final String responseBody = loadJson("add_products_response.json", getClass());
        when(mockService.addProductsToShoppingCart(any(), anyMap())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
    }

    @Test
    void whenDeleteAtBasePath_ThenInvokeDeactivateShoppingCartByUsernameMethodAndProcessResponse() throws Exception {
        doNothing().when(mockService).deactivateShoppingCart(any());

        mvc.perform(delete(BASE_PATH)
                        .param("username", USERNAME_A))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).deactivateShoppingCart(USERNAME_A);
    }

    @Test
    void whenPostAtRemoveEndpoint_ThenInvokeDeleteProductsFromShoppingCartMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("delete_products_request.json", getClass());
        final String responseBody = loadJson("delete_products_response.json", getClass());
        when(mockService.deleteProductsFromShoppingCart(any(), anySet())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        mvc.perform(post(BASE_PATH + "/remove")
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
    }

    @Test
    void whenPostAtChangeQuantityEndpoint_ThenInvokeChangeProductQuantityMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("change_product_quantity_request.json", getClass());
        final String responseBody = loadJson("change_product_quantity_response.json", getClass());
        when(mockService.changeProductQuantity(any(), any())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        mvc.perform(post(BASE_PATH + "/change-quantity")
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockService).changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
    }

    @Test
    void whenPostAtBookingEndpoint_ThenInvokeBookProductsInWarehouseMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("book_products_in_warehouse_response.json", getClass());
        when(mockService.bookProductsInWarehouse(any())).thenReturn(getTestBookedProductsDto());

        mvc.perform(post(BASE_PATH + "/booking")
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody));

        verify(mockService).bookProductsInWarehouse(USERNAME_A);
    }

    @Test
    void whenNotAuthorizedUserException_ThenInvokeControllerExceptionHandler() throws Exception {
        when(mockService.getShoppingCartByUsername(any()))
                .thenThrow(new NotAuthorizedUserException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(get(BASE_PATH)
                        .param("username", WRONG_USERNAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NotAuthorizedUserException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.UNAUTHORIZED.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE))
                );

        verify(mockService).getShoppingCartByUsername(WRONG_USERNAME);
    }

    @Test
    void whenShoppingCartDeactivatedException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("add_products_request.json", getClass());
        when(mockService.addProductsToShoppingCart(any(), anyMap()))
                .thenThrow(new ShoppingCartDeactivatedException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isLocked(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                ShoppingCartDeactivatedException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.LOCKED.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE))
                );

        verify(mockService).addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd());
    }

    @Test
    void whenNoProductsInShoppingCartException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("delete_products_request.json", getClass());
        when(mockService.deleteProductsFromShoppingCart(any(), anySet()))
                .thenThrow(new NoProductsInShoppingCartException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/remove")
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoProductsInShoppingCartException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE))
                );

        verify(mockService).deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete());
    }

    @Test
    void whenProductInShoppingCartNotInWarehouse_ThenInvokeControllerExceptionHandler() throws Exception {
        when(mockService.bookProductsInWarehouse(any()))
                .thenThrow(new ProductInShoppingCartNotInWarehouse(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/booking")
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                ProductInShoppingCartNotInWarehouse.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE))
                );

        verify(mockService).bookProductsInWarehouse(USERNAME_A);
    }
}