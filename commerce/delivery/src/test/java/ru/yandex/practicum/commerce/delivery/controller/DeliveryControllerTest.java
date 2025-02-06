package ru.yandex.practicum.commerce.delivery.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.delivery.util.LogListener;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.DELIVERY_COST;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDeliveryDto;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestNewDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestNewDeliveryDto;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.delivery.util.TestUtils.assertLogs;

class DeliveryControllerTest {

    private static final LogListener logListener = new LogListener(DeliveryController.class);
    private DeliveryService mockService;
    private DeliveryMapper mockMapper;
    private InOrder inOrder;

    private DeliveryController controller;

    @BeforeEach
    void setUp() {
        mockService = Mockito.mock(DeliveryService.class);
        mockMapper = Mockito.mock(DeliveryMapper.class);
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new DeliveryController(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void whenPlanDelivery_ThenMapDtoToEntityAndPassToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockMapper.mapToEntity(any())).thenReturn(getTestNewDelivery());
        when(mockService.planDelivery(any())).thenReturn(getTestDelivery());
        when(mockMapper.mapToDto(any())).thenReturn(getTestDeliveryDto());

        final DeliveryDto dto = controller.planDelivery(getTestNewDeliveryDto());

        inOrder.verify(mockMapper).mapToEntity(getTestNewDeliveryDto());
        inOrder.verify(mockService).planDelivery(getTestNewDelivery());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestDelivery()));
        assertThat(dto, equalTo(getTestDeliveryDto()));
        assertLogs(logListener.getEvents(), "plan_delivery.json", getClass());
    }

    @Test
    void whenPickDelivery_ThenPassOrderIdToServiceAndLog() throws Exception {
        doNothing().when(mockService).pickDelivery(any());

        controller.pickDelivery(ORDER_ID_A);

        verify(mockService).pickDelivery(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "pick_delivery.json", getClass());
    }

    @Test
    void whenConfirmDelivery_ThenPassOrderIdToServiceAndLog() throws Exception {
        doNothing().when(mockService).confirmDelivery(any());

        controller.confirmDelivery(ORDER_ID_A);

        verify(mockService).confirmDelivery(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSignalDeliveryFailure_ThenPassOrderIdYoServiceAndLog() throws Exception {
        doNothing().when(mockService).signalDeliveryFailure(any());

        controller.signalDeliveryFailure(ORDER_ID_A);

        verify(mockService).signalDeliveryFailure(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "signal_delivery_failure.json", getClass());
    }

    @Test
    void whenCalculateDeliveryCost_ThenPassOrderDtoToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.calculateDeliveryCost(any())).thenReturn(DELIVERY_COST);

        final BigDecimal deliveryCost = controller.calculateDeliveryCost(getTestOrderDto());

        verify(mockService).calculateDeliveryCost(getTestOrderDto());
        assertThat(deliveryCost, equalTo(DELIVERY_COST));
        assertLogs(logListener.getEvents(), "calculate_delivery_cost.json", getClass());
    }
}