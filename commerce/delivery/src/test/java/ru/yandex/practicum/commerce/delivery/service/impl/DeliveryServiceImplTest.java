package ru.yandex.practicum.commerce.delivery.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.delivery.service.OrderService;
import ru.yandex.practicum.commerce.delivery.service.WarehouseService;
import ru.yandex.practicum.commerce.delivery.util.LogListener;
import ru.yandex.practicum.commerce.delivery.util.UUIDGenerator;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.exception.OrderDeliveryAlreadyExistsException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.COST_SCALE;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.DELIVERY_ID;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDeliveryConfirmed;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDeliveryFailed;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestDeliveryPicked;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestNewDelivery;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDtoAssembled;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDtoDelivered;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestOrderDtoNotDelivered;
import static ru.yandex.practicum.commerce.delivery.util.TestModels.getTestShippedToDeliveryRequest;
import static ru.yandex.practicum.commerce.delivery.util.TestUtils.assertLogs;

class DeliveryServiceImplTest {

    private static final LogListener logListener = new LogListener(DeliveryServiceImpl.class);
    private OrderService mockOrderService;
    private WarehouseService mockWarehouseService;
    private DeliveryRepository mockRepository;
    private UUIDGenerator mockUUIDGenerator;
    private InOrder inOrder;

    private DeliveryService service;

    private static Stream<TestCaseSeed> getTestCaseSeeds() {
        return Stream.of(
                new TestCaseSeed((order, delivery) -> order.setState(order.getState()), 3240L),
                new TestCaseSeed((order, delivery) -> delivery.getFromAddress().setCity("ADDRESS_2"), 3960L),
                new TestCaseSeed((order, delivery) -> order.setFragile(false), 3000L),
                new TestCaseSeed((order, delivery) -> order.setDeliveryWeight(BigDecimal.TEN), 3096L),
                new TestCaseSeed((order, delivery) -> order.setDeliveryVolume(BigDecimal.TEN), 2184L),
                new TestCaseSeed((order, delivery) -> delivery.getToAddress().setStreet("ADDRESS_1"), 2700L)
        );
    }

    private static Stream<Arguments> getTestCases() {
        return getTestCaseSeeds()
                .map(seed -> {
                    OrderDto order = getTestOrderDto();
                    Delivery delivery = getTestDelivery();
                    seed.testDataPreprocessor().accept(order, delivery);
                    BigDecimal expectedCost = BigDecimal.valueOf(seed.expectedCost(), COST_SCALE);
                    return Arguments.of(order, delivery, expectedCost);
                });
    }

    @BeforeEach
    void setUp() {
        mockOrderService = Mockito.mock(OrderService.class);
        mockWarehouseService = Mockito.mock(WarehouseService.class);
        mockRepository = Mockito.mock(DeliveryRepository.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        inOrder = Mockito.inOrder(mockOrderService, mockWarehouseService, mockRepository, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new DeliveryServiceImpl(mockOrderService, mockWarehouseService, mockRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockOrderService, mockWarehouseService, mockRepository, mockUUIDGenerator);
    }

    @Test
    void whenPlanDeliveryAndDeliveryForOrderAlreadyExists_ThenThrowException() {
        when(mockRepository.existsByOrderId(any())).thenReturn(true);

        final OrderDeliveryAlreadyExistsException exception = assertThrows(OrderDeliveryAlreadyExistsException.class,
                () -> service.planDelivery(getTestNewDelivery()));

        verify(mockRepository).existsByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Delivery for order " + ORDER_ID_A + " already exists"));
    }

    @Test
    void whenPlanDeliveryAndDeliveryForOrderNotExist_ThenAssignIdAndSaveDeliveryAndReturnItAndLog()
            throws Exception {
        when(mockRepository.existsByOrderId(any())).thenReturn(false);
        when(mockUUIDGenerator.getNewUUID()).thenReturn(DELIVERY_ID);
        when(mockRepository.save(any())).thenReturn(getTestDelivery());

        final Delivery delivery = service.planDelivery(getTestNewDelivery());

        inOrder.verify(mockRepository).existsByOrderId(ORDER_ID_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestDelivery())));
        assertThat(delivery, samePropertyValuesAs(getTestDelivery()));
        assertLogs(logListener.getEvents(), "plan_delivery.json", getClass());
    }

    @Test
    void whenPickDeliveryAndDeliveryForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoDeliveryFoundException exception = assertThrows(NoDeliveryFoundException.class,
                () -> service.pickDelivery(ORDER_ID_A));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Delivery for order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenPickDeliveryAndDeliveryForOrderExist_ThenUpdateDeliveryStatusInRepositoryWarehouseOrderAndLog()
            throws Exception {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(getTestDelivery()));
        when(mockRepository.save(any())).thenReturn(getTestDeliveryPicked());
        doNothing().when(mockWarehouseService).shippedToDelivery(any());
        when(mockOrderService.assembled(any())).thenReturn(getTestOrderDtoAssembled());

        service.pickDelivery(ORDER_ID_A);

        inOrder.verify(mockRepository).findByOrderId(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestDeliveryPicked())));
        inOrder.verify(mockWarehouseService).shippedToDelivery(getTestShippedToDeliveryRequest());
        inOrder.verify(mockOrderService).assembled(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "pick_delivery.json", getClass());
    }

    @Test
    void whenConfirmDeliveryAndDeliveryForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoDeliveryFoundException exception = assertThrows(NoDeliveryFoundException.class,
                () -> service.confirmDelivery(ORDER_ID_A));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Delivery for order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenConfirmDeliveryAndDeliveryForOrderExist_ThenUpdateDeliveryStatusInRepositoryOrderAndLog() throws
            Exception {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(getTestDeliveryPicked()));
        when(mockRepository.save(any())).thenReturn(getTestDeliveryConfirmed());
        when(mockOrderService.delivery(any())).thenReturn(getTestOrderDtoDelivered());

        service.confirmDelivery(ORDER_ID_A);

        inOrder.verify(mockRepository).findByOrderId(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestDeliveryConfirmed())));
        inOrder.verify(mockOrderService).delivery(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSignalDeliveryFailureAndDeliveryForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoDeliveryFoundException exception = assertThrows(NoDeliveryFoundException.class,
                () -> service.signalDeliveryFailure(ORDER_ID_A));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Delivery for order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenSignalDeliveryFailureAndDeliveryForOrderExist_ThenUpdateDeliveryStatusInRepositoryOrderAndLog()
            throws Exception {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(getTestDeliveryPicked()));
        when(mockRepository.save(any())).thenReturn(getTestDeliveryFailed());
        when(mockOrderService.deliveryFailed(any())).thenReturn(getTestOrderDtoNotDelivered());

        service.signalDeliveryFailure(ORDER_ID_A);

        inOrder.verify(mockRepository).findByOrderId(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestDeliveryFailed())));
        inOrder.verify(mockOrderService).deliveryFailed(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "signal_delivery_failure.json", getClass());
    }

    @Test
    void whenCalculateDeliveryCostAndDeliveryForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoDeliveryFoundException exception = assertThrows(NoDeliveryFoundException.class,
                () -> service.calculateDeliveryCost(getTestOrderDto()));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Delivery for order " + ORDER_ID_A + " does not exist"));
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    void whenCalculateDeliveryCostAndDeliveryForOrderExist_ThenReturnCorrectDeliveryCost(final OrderDto order,
            final Delivery delivery, final BigDecimal expectedCost) {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(delivery));

        final BigDecimal deliveryCost = service.calculateDeliveryCost(order);

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(deliveryCost, equalTo(expectedCost));
    }

    private record TestCaseSeed(BiConsumer<OrderDto, Delivery> testDataPreprocessor, long expectedCost) {

    }
}