package ru.yandex.practicum.commerce.order.controller;

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
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.order.mapper.AddressMapper;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.service.OrderService;

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
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestAddressA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestNewOrder;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestNewOrderDto;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAPaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAUnpaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderB;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoAPaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoAUnpaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoB;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestPageable;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestShoppingCartA;
import static ru.yandex.practicum.commerce.order.util.TestUtils.loadJson;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerIT {

    private static final String BASE_PATH = "/api/v1/order";
    private InOrder inOrder;

    @MockBean
    private OrderService mockOrderService;

    @MockBean
    private OrderMapper mockOrderMapper;

    @Captor
    private ArgumentCaptor<List<Order>> ordersCaptor;

    @MockBean
    private AddressMapper mockAddressMapper;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockOrderService, mockOrderMapper, mockAddressMapper);
        inOrder = Mockito.inOrder(mockOrderService, mockOrderMapper, mockAddressMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockOrderService, mockOrderMapper, mockAddressMapper);
    }

    @Test
    void whenPutAtBasePath_ThenInvokeAddNewOrderMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("create_order_request.json", getClass());
        final String responseBody = loadJson("create_order_response.json", getClass());
        when(mockAddressMapper.mapToEntity(any())).thenReturn(getTestAddressA());
        when(mockOrderService.addNewOrder(any(), any(), any())).thenReturn(getTestNewOrder());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestNewOrderDto());

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

        inOrder.verify(mockAddressMapper).mapToEntity(getTestAddressDtoA());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA());
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestNewOrder()));
    }

    @Test
    void whenGetAtBasePath_ThenInvokeGetOrdersByUserNameMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_orders_response.json", getClass());
        when(mockOrderService.findOrdersByUsername(any(), any())).thenReturn(List.of(getTestOrderA(), getTestOrderB()));
        when(mockOrderMapper.mapToDto(anyList())).thenReturn(List.of(getTestOrderDtoA(), getTestOrderDtoB()));

        mvc.perform(get(BASE_PATH)
                        .param("username", USERNAME_A)
                        .param("page", String.valueOf(getTestPageable().getPage()))
                        .param("size", String.valueOf(getTestPageable().getSize()))
                        .param("sort", getTestPageable().getSort())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).findOrdersByUsername(USERNAME_A, PAGEABLE);
        inOrder.verify(mockOrderMapper).mapToDto(ordersCaptor.capture());
        assertThat(ordersCaptor.getValue(), contains(samePropertyValuesAs(getTestOrderA()),
                samePropertyValuesAs(getTestOrderB())));
    }

    @Test
    void whenPostAtPaymentEndpoint_ThenInvokeConfirmPaymentMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        final String responseBody = loadJson("confirm_payment_response.json", getClass());
        when(mockOrderService.confirmPayment(any())).thenReturn(getTestOrderAPaid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoAPaid());

        mvc.perform(post(BASE_PATH + "/payment")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderAPaid()));
    }

    @Test
    void whenPostAtPaymentFiledEndpoint_ThenInvokeSetPaymentFailedMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        final String responseBody = loadJson("set_payment_failed_response.json", getClass());
        when(mockOrderService.setPaymentFailed(any())).thenReturn(getTestOrderAUnpaid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoAUnpaid());

        mvc.perform(post(BASE_PATH + "/payment/failed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).setPaymentFailed(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderAUnpaid()));
    }

    @Test
    void whenNotAuthorizedUserException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("create_order_request.json", getClass());
        when(mockAddressMapper.mapToEntity(any())).thenReturn(getTestAddressA());
        when(mockOrderService.addNewOrder(any(), any(), any()))
                .thenThrow(new NotAuthorizedUserException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME_A)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NotAuthorizedUserException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.UNAUTHORIZED.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockAddressMapper).mapToEntity(getTestAddressDtoA());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA());
    }

    @Test
    void whenNoSpecifiedProductInWarehouseException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("create_order_request.json", getClass());
        when(mockAddressMapper.mapToEntity(any())).thenReturn(getTestAddressA());
        when(mockOrderService.addNewOrder(any(), any(), any()))
                .thenThrow(new NoSpecifiedProductInWarehouseException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
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
                                NoSpecifiedProductInWarehouseException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockAddressMapper).mapToEntity(getTestAddressDtoA());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA());
    }

    @Test
    void whenNoOrderFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        when(mockOrderService.confirmPayment(any())).thenThrow(new NoOrderFoundException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/payment")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoOrderFoundException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID_A);
    }
}