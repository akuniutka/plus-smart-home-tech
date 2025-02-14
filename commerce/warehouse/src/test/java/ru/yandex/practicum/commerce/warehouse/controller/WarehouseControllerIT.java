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
import ru.yandex.practicum.commerce.exception.ApiExceptions;
import ru.yandex.practicum.commerce.exception.NoOrderBookingFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.BookingMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddProductToWarehouseRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAssemblyProductsForOrderRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestBookedProducts;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestDeliveryParams;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestNewProductDto;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShippedToDeliveryRequest;
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

    @MockBean
    private BookingMapper mockBookingMapper;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProductService, mockAddressService, mockAddressService, mockBookingMapper);
        inOrder = Mockito.inOrder(mockProductService, mockAddressService, mockProductMapper, mockBookingMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockProductService, mockAddressService, mockProductMapper, mockBookingMapper);
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
    void whenPostAtAddEndpoint_ThenInvokeIncreaseProductQuantityMethod() throws Exception {
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
    void whenPostAtCheckEndpoint_ThenInvokeCheckProductsAvailabilityMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("check_products_availability_request.json", getClass());
        final String responseBody = loadJson("check_products_availability_response.json", getClass());
        when(mockProductService.checkProductsAvailability(any())).thenReturn(getTestDeliveryParams());
        when(mockBookingMapper.mapToDto(any())).thenReturn(getTestBookedProducts());

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

        inOrder.verify(mockProductService).checkProductsAvailability(getTestShoppingCart());
        inOrder.verify(mockBookingMapper).mapToDto(getTestDeliveryParams());
    }

    @Test
    void whenPostAtAssemblyEndpoint_ThenInvokeBookProductsMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("book_products_request.json", getClass());
        final String responseBody = loadJson("book_products_response.json", getClass());
        when(mockProductService.bookProducts(any())).thenReturn(getTestDeliveryParams());
        when(mockBookingMapper.mapToDto(any())).thenReturn(getTestBookedProducts());

        mvc.perform(post(BASE_PATH + "/assembly")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockProductService).bookProducts(getTestAssemblyProductsForOrderRequest());
        inOrder.verify(mockBookingMapper).mapToDto(getTestDeliveryParams());
    }

    @Test
    void whenPostAtShippedEndpoint_ThenInvokeShippedToDeliveryMethodAndResponseWith200OK() throws Exception {
        final String requestBody = loadJson("shipped_to_delivery_request.json", getClass());
        doNothing().when(mockProductService).shippedToDelivery(any());

        mvc.perform(post(BASE_PATH + "/shipped")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockProductService).shippedToDelivery(getTestShippedToDeliveryRequest());
    }

    @Test
    void whenGetAtAddressEndpoint_ThenInvokerGetWarehouseAddressMethodAndProcessResponse() throws Exception {
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
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                SpecifiedProductAlreadyInWarehouseException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockProductMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockProductService).addNewProduct(refEq(getTestProductA()));
    }

    @Test
    void whenProductInShoppingCartNotInWarehouse_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("check_products_availability_request.json", getClass());
        when(mockProductService.checkProductsAvailability(any()))
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
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                ProductInShoppingCartNotInWarehouse.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).checkProductsAvailability(getTestShoppingCart());
    }

    @Test
    void whenProductInShoppingCartLowQuantityInWarehouse_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("check_products_availability_request.json", getClass());
        when(mockProductService.checkProductsAvailability(any()))
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
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                ProductInShoppingCartLowQuantityInWarehouse.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).checkProductsAvailability(getTestShoppingCart());
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
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoSpecifiedProductInWarehouseException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).increaseProductQuantity(getTestAddProductToWarehouseRequest());
    }

    @Test
    void whenNoOrderBookingFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("shipped_to_delivery_request.json", getClass());
        doThrow(new NoOrderBookingFoundException(TEST_EXCEPTION_MESSAGE))
                .when(mockProductService).shippedToDelivery(any());

        mvc.perform(post(BASE_PATH + "/shipped")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoOrderBookingFoundException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockProductService).shippedToDelivery(getTestShippedToDeliveryRequest());
    }
}