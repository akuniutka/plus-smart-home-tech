package ru.yandex.practicum.commerce.payment.controller;

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
import ru.yandex.practicum.commerce.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.exception.OrderPaymentAlreadyExistsException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.commerce.payment.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PRODUCT_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.payment.util.TestModels.TOTAL_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutCosts;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutTotalCost;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPayment;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPaymentDto;
import static ru.yandex.practicum.commerce.payment.util.TestUtils.loadJson;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerIT {

    private static final String BASE_PATH = "/api/v1/payment";
    private InOrder inOrder;

    @MockBean
    private PaymentService mockService;

    @MockBean
    private PaymentMapper mockMapper;

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
    void whenPostAtBasePath_ThenInvokeCreatePaymentMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("create_payment_request.json", getClass());
        final String responseBody = loadJson("create_payment_response.json", getClass());
        when(mockService.createPayment(any())).thenReturn(getTestPayment());
        when(mockMapper.mapToDto(any())).thenReturn(getTestPaymentDto());

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

        inOrder.verify(mockService).createPayment(getTestOrderDto());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestPayment()));
    }

    @Test
    void whenPostAtProductCostEndpoint_ThenInvokeCalculateProductCostMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("calculate_product_cost_request.json", getClass());
        final String responseBody = PRODUCT_COST.toString();
        when(mockService.calculateProductCost(any())).thenReturn(PRODUCT_COST);

        mvc.perform(post(BASE_PATH + "/productCost")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockService).calculateProductCost(getTestOrderDtoWithoutCosts());
    }

    @Test
    void whenPostAtTotalCostEndpoint_ThenInvokeCalculateTotalCostMethodAndProcessResponse() throws Exception {
        final String requestBody = loadJson("calculate_total_cost_request.json", getClass());
        final String responseBody = TOTAL_COST.toString();
        when(mockService.calculateTotalCost((any()))).thenReturn(TOTAL_COST);

        mvc.perform(post(BASE_PATH + "/totalCost")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(responseBody, true));

        verify(mockService).calculateTotalCost(getTestOrderDtoWithoutTotalCost());
    }

    @Test
    void whenPostAtRefundEndpoint_ThenInvokeConfirmPaymentMethod() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        doNothing().when(mockService).confirmPayment(any());

        mvc.perform(post(BASE_PATH + "/refund")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).confirmPayment(ORDER_ID_A);
    }

    @Test
    void whenPostAtFailedEndpoint_ThenInvokeSignalPaymentFailureMethod() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        doNothing().when(mockService).signalPaymentFailure(any());

        mvc.perform(post(BASE_PATH + "/failed")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(mockService).signalPaymentFailure(ORDER_ID_A);
    }

    @Test
    void whenOrderPaymentAlreadyExistsException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("create_payment_request.json", getClass());
        when(mockService.createPayment(any()))
                .thenThrow(new OrderPaymentAlreadyExistsException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                OrderPaymentAlreadyExistsException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        inOrder.verify(mockService).createPayment(getTestOrderDto());
    }

    @Test
    void whenNotEnoughInfoInOrderToCalculateException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = loadJson("calculate_product_cost_request.json", getClass());
        when(mockService.calculateProductCost(any()))
                .thenThrow(new NotEnoughInfoInOrderToCalculateException(TEST_EXCEPTION_MESSAGE));

        mvc.perform(post(BASE_PATH + "/productCost")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NotEnoughInfoInOrderToCalculateException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.BAD_REQUEST.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockService).calculateProductCost(getTestOrderDtoWithoutCosts());
    }

    @Test
    void whenNoPaymentFoundException_ThenInvokeControllerExceptionHandler() throws Exception {
        final String requestBody = "\"%s\"".formatted(ORDER_ID_A);
        doThrow(new NoPaymentFoundException(TEST_EXCEPTION_MESSAGE)).when(mockService).confirmPayment(any());

        mvc.perform(post(BASE_PATH + "/refund")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header().string(ApiExceptions.API_EXCEPTION_HEADER,
                                NoPaymentFoundException.class.getSimpleName()),
                        jsonPath("$.httpStatus", equalTo(HttpStatus.NOT_FOUND.name())),
                        jsonPath("$.userMessage", equalTo(TEST_EXCEPTION_MESSAGE)));

        verify(mockService).confirmPayment(ORDER_ID_A);
    }
}