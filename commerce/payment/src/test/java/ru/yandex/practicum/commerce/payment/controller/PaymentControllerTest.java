package ru.yandex.practicum.commerce.payment.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.commerce.payment.util.LogListener;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.payment.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PRODUCT_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.TOTAL_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutCosts;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutTotalCost;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPayment;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPaymentDto;
import static ru.yandex.practicum.commerce.payment.util.TestUtils.assertLogs;

class PaymentControllerTest {

    private static final LogListener logListener = new LogListener(PaymentController.class);
    private PaymentService mockService;
    private PaymentMapper mockMapper;
    private InOrder inOrder;

    private PaymentController controller;

    @BeforeEach
    void setUp() {
        mockService = Mockito.mock(PaymentService.class);
        mockMapper = Mockito.mock(PaymentMapper.class);
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new PaymentController(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void whenCreatePayment_ThenPassOrderDtoToServiceAndMapServiceResponseAndReturnItAdnLog() throws Exception {
        when(mockService.createPayment(any())).thenReturn(getTestPayment());
        when(mockMapper.mapToDto(any())).thenReturn(getTestPaymentDto());

        final PaymentDto dto = controller.createPayment(getTestOrderDto());

        inOrder.verify(mockService).createPayment(getTestOrderDto());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestPayment()));
        assertThat(dto, equalTo(getTestPaymentDto()));
        assertLogs(logListener.getEvents(), "create_payment.json", getClass());
    }

    @Test
    void whenCalculateProductCost_ThenPassOrderDtoToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.calculateProductCost(any())).thenReturn(PRODUCT_COST);

        final BigDecimal productCost = controller.calculateProductCost(getTestOrderDtoWithoutCosts());

        verify(mockService).calculateProductCost(getTestOrderDtoWithoutCosts());
        assertThat(productCost, equalTo(PRODUCT_COST));
        assertLogs(logListener.getEvents(), "calculate_product_cost.json", getClass());
    }

    @Test
    void whenCalculateTotalCost_ThenPassOrderDtoToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.calculateTotalCost((any()))).thenReturn(TOTAL_COST);

        final BigDecimal totalCost = controller.calculateTotalCost(getTestOrderDtoWithoutTotalCost());

        verify(mockService).calculateTotalCost(getTestOrderDtoWithoutTotalCost());
        assertThat(totalCost, equalTo(TOTAL_COST));
        assertLogs(logListener.getEvents(), "calculate_total_cost.json", getClass());
    }

    @Test
    void whenConfirmPayment_ThenPassOrderIdToServiceAndLog() throws Exception {
        doNothing().when(mockService).confirmPayment(any());

        controller.confirmPayment(ORDER_ID_A);

        verify(mockService).confirmPayment(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSignalPaymentFailure_ThenPassOrderIdToServiceAndLog() throws Exception {
        doNothing().when(mockService).signalPaymentFailure(any());

        controller.signalPaymentFailure(ORDER_ID_A);

        verify(mockService).signalPaymentFailure(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "signal_payment_failure.json", getClass());
    }
}