package ru.yandex.practicum.commerce.delivery.controller;

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
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.exception.ApiExceptions;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.exception.OrderDeliveryAlreadyExistsException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.DELIVERY_COST;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDeliveryDto;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestNewDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestNewDeliveryDto;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.delivery.util.TestUtils.loadJson;

@WebMvcTest(controllers = DeliveryController.class)
class DeliveryControllerIT {

    private static final String BASE_PATH = "/api/v1/delivery";
    private InOrder inOrder;

    @MockBean
    private DeliveryService mockService;

    @MockBean
    private DeliveryMapper mockMapper;

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
    void whenPutAtBasePath_ThenInvokePlanDeliveryMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("plan_delivery_request.json", getClass());
        final String responseBody = loadJson("plan_delivery_response.json", getClass());
        when(mockMapper.mapToEntity(any())).thenReturn(getTestNewDelivery());
        when(mockService.planDelivery(any())).thenReturn(getTestDelivery());
        when(mockMapper.mapToDto(any())).thenReturn(getTestDeliveryDto());

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

        inOrder.verify(mockMapper).mapToEntity(getTestNewDeliveryDto());
        inOrder.verify(mockService).planDelivery(getTestNewDelivery());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestDelivery()));
    }

    @Test
    void whenPostAtPickedEndpoint_ThenInvokePickDeliveryMethod() throws Exception {
        final String requestBody = "\"%S\"".formatted(ORDER_ID_A);
        doNothing().when(mockService).pickDelivery(any());

        mvc.perform(post(BASE_PATH + "/picked")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).pickDelivery(ORDER_ID_A);
    }

    @Test
    void whenPostAtSuccessfulEndpoint_ThenInvokeConfirmDeliveryMethod() throws Exception {
        final String requestBody = "\"%S\"".formatted(ORDER_ID_A);
        doNothing().when(mockService).confirmDelivery(any());

        mvc.perform(post(BASE_PATH + "/successful")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).confirmDelivery(ORDER_ID_A);
    }

    @Test
    void whenPostAtFailedEndpoint_ThenInvokeSignalDeliveryFailureMethod() throws Exception {
        final String requestBody = "\"%S\"".formatted(ORDER_ID_A);
        doNothing().when(mockService).signalDeliveryFailure(any());

        mvc.perform(post(BASE_PATH + "/failed")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).signalDeliveryFailure(ORDER_ID_A);
    }

    @Test
    void whenPostAtCostEndpoint_ThenInvokeCalculateDeliveryCostMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("calculate_delivery_cost.json", getClass());
        final String responseBody = DELIVERY_COST.toString();
        when(mockService.calculateDeliveryCost(any())).thenReturn(DELIVERY_COST);

        mvc.perform(post(BASE_PATH + "/cost")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockService).calculateDeliveryCost(getTestOrderDto());
    }

    @Test
    void whenOrderDeliveryAlreadyExistsException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("plan_delivery_request.json", getClass());
        when(mockMapper.mapToEntity(any())).thenReturn(getTestNewDelivery());
        when(mockService.planDelivery(any()))
                .thenThrow(new OrderDeliveryAlreadyExistsException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(put(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                OrderDeliveryAlreadyExistsException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockMapper).mapToEntity(getTestNewDeliveryDto());
        inOrder.verify(mockService).planDelivery(getTestNewDelivery());
    }

    @Test
    void whenNoDeliveryFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = "\"%S\"".formatted(ORDER_ID_A);
        doThrow(new NoDeliveryFoundException(TEST_EXCEPTION_MESSAGE)).when(mockService).pickDelivery(any());

        mvc.perform(post(BASE_PATH + "/picked")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoDeliveryFoundException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockService).pickDelivery(ORDER_ID_A);
    }
}