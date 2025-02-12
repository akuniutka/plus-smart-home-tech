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
import ru.yandex.practicum.commerce.order.util.TestAddress;
import ru.yandex.practicum.commerce.order.util.TestAddressDto;
import ru.yandex.practicum.commerce.order.util.TestOrder;
import ru.yandex.practicum.commerce.order.util.TestOrderDto;
import ru.yandex.practicum.commerce.order.util.TestPageable;
import ru.yandex.practicum.commerce.order.util.TestShoppingCartDto;

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
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME;
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
        when(mockAddressMapper.mapToEntity(any())).thenReturn(TestAddress.create());
        when(mockOrderService.addNewOrder(any(), any(), any())).thenReturn(TestOrder.create());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.create());

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockAddressMapper).mapToEntity(TestAddressDto.create());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create());
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.create()));
    }

    @Test
    void whenGetAtBasePathWithOrderId_ThenInvokeGetOrderByIdMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_order_by_id_response.json", getClass());
        when(mockOrderService.getOrderById(any())).thenReturn(TestOrder.completed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.completed());

        mvc.perform(get(BASE_PATH + "/" + ORDER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).getOrderById(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.completed()));
    }

    @Test
    void whenGetAtBasePath_ThenInvokeGetOrdersByUserNameMethodAndProcessResponse() throws Exception {
        final String responseBody = loadJson("get_orders_response.json", getClass());
        when(mockOrderService.findOrdersByUsername(any(), any())).thenReturn(
                List.of(TestOrder.completed(), TestOrder.other()));
        when(mockOrderMapper.mapToDto(anyList())).thenReturn(List.of(TestOrderDto.completed(), TestOrderDto.other()));

        mvc.perform(get(BASE_PATH)
                        .param("username", USERNAME)
                        .param("page", String.valueOf(TestPageable.PAGE))
                        .param("size", String.valueOf(TestPageable.SIZE))
                        .param("sort", TestPageable.create().getSort())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).findOrdersByUsername(USERNAME, PAGEABLE);
        inOrder.verify(mockOrderMapper).mapToDto(ordersCaptor.capture());
        assertThat(ordersCaptor.getValue(), contains(samePropertyValuesAs(TestOrder.completed()),
                samePropertyValuesAs(TestOrder.other())));
    }

    @Test
    void whenPostAtCalculateProductEndpoint_ThenInvokeCalculateProductCostMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("calculate_product_cost_response.json", getClass());
        when(mockOrderService.calculateProductCost(any())).thenReturn(TestOrder.withProductPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withProductPrice());

        mvc.perform(post(BASE_PATH + "/calculate/product")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).calculateProductCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withProductPrice()));
    }

    @Test
    void whenPostAtCalculateDeliveryEndpoint_ThenInvokeCalculateDeliveryCostMethodAndProcessResponse()
            throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("calculate_delivery_cost_response.json", getClass());
        when(mockOrderService.calculateDeliveryCost(any())).thenReturn(TestOrder.withDeliveryPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withDeliveryPrice());

        mvc.perform(post(BASE_PATH + "/calculate/delivery")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).calculateDeliveryCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withDeliveryPrice()));
    }

    @Test
    void whenPostAtCalculateTotalEndpoint_ThenInvokeCalculateTotalCostMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("calculate_total_cost_response.json", getClass());
        when(mockOrderService.calculateTotalCost(any())).thenReturn(TestOrder.withTotalPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withTotalPrice());

        mvc.perform(post(BASE_PATH + "/calculate/total")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).calculateTotalCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withTotalPrice()));
    }

    @Test
    void whenPostAtDeliveryEndpoint_ThenInvokeConfirmAssemblyMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("confirm_assembly_response.json", getClass());
        when(mockOrderService.confirmAssembly(any())).thenReturn(TestOrder.assembled());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.assembled());

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

        inOrder.verify(mockOrderService).confirmAssembly(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.assembled()));
    }

    @Test
    void whenPostAtAssemblyFailedEndpoint_ThenInvokeSetAssemblyFailedMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("set_assembly_failed_response.json", getClass());
        when(mockOrderService.setAssemblyFailed(any())).thenReturn(TestOrder.withAssemblyFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withAssemblyFailed());

        mvc.perform(post(BASE_PATH + "/assembly/failed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).setAssemblyFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withAssemblyFailed()));
    }

    @Test
    void whenPostAtPaymentEndpoint_ThenInvokeConfirmPaymentMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("confirm_payment_response.json", getClass());
        when(mockOrderService.confirmPayment(any())).thenReturn(TestOrder.paid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.paid());

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

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.paid()));
    }

    @Test
    void whenPostAtPaymentFailedEndpoint_ThenInvokeSetPaymentFailedMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("set_payment_failed_response.json", getClass());
        when(mockOrderService.setPaymentFailed(any())).thenReturn(TestOrder.withPaymentFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withPaymentFailed());

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

        inOrder.verify(mockOrderService).setPaymentFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withPaymentFailed()));
    }

    @Test
    void whenPostAtDeliveryEndpoint_ThenInvokeConfirmDeliveryMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("confirm_delivery_response.json", getClass());
        when(mockOrderService.confirmDelivery(any())).thenReturn(TestOrder.delivered());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.delivered());

        mvc.perform(post(BASE_PATH + "/delivery")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).confirmDelivery(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.delivered()));
    }

    @Test
    void whenPostAtDeliveryFailedEndpoint_ThenInvokeSetDeliveryFailedMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("set_delivery_failed_response.json", getClass());
        when(mockOrderService.setDeliveryFailed(any())).thenReturn(TestOrder.withDeliveryFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withDeliveryFailed());

        mvc.perform(post(BASE_PATH + "/delivery/failed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).setDeliveryFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withDeliveryFailed()));
    }

    @Test
    void whenPostAtCompletedEndpoint_ThenInvokeCompeteOrderMethodAndProcessResponse() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
        final String responseBody = loadJson("complete_order_response.json", getClass());
        when(mockOrderService.completeOrder(any())).thenReturn(TestOrder.completed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.completed());

        mvc.perform(post(BASE_PATH + "/completed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        inOrder.verify(mockOrderService).completeOrder(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.completed()));
    }

    @Test
    void whenNotAuthorizedUserException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("create_order_request.json", getClass());
        when(mockAddressMapper.mapToEntity(any())).thenReturn(TestAddress.create());
        when(mockOrderService.addNewOrder(any(), any(), any()))
                .thenThrow(new NotAuthorizedUserException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME)
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

        inOrder.verify(mockAddressMapper).mapToEntity(TestAddressDto.create());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create());
    }

    @Test
    void whenNoSpecifiedProductInWarehouseException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("create_order_request.json", getClass());
        when(mockAddressMapper.mapToEntity(any())).thenReturn(TestAddress.create());
        when(mockOrderService.addNewOrder(any(), any(), any()))
                .thenThrow(new NoSpecifiedProductInWarehouseException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
                        .param("username", USERNAME)
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

        inOrder.verify(mockAddressMapper).mapToEntity(TestAddressDto.create());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create());
    }

    @Test
    void whenNoOrderFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID);
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

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID);
    }
}