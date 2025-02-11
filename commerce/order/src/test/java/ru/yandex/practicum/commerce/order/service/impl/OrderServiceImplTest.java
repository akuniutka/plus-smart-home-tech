package ru.yandex.practicum.commerce.order.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.commerce.order.service.WarehouseService;
import ru.yandex.practicum.commerce.order.util.LogListener;
import ru.yandex.practicum.commerce.order.util.UUIDGenerator;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.WRONG_USERNAME;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestAddressA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestBookedProductsDto;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestNewOrder;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderADelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderANew;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderANotAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderANotDelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAPaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAUnpaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderB;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestShoppingCartA;
import static ru.yandex.practicum.commerce.order.util.TestUtils.assertLogs;

class OrderServiceImplTest {

    private static final LogListener logListener = new LogListener(OrderServiceImpl.class);
    private WarehouseService mockWarehouseService;
    private OrderRepository mockRepository;
    private UUIDGenerator mockUUIDGenerator;
    private InOrder inOrder;

    private OrderService service;

    @BeforeEach
    void setUp() {
        mockWarehouseService = Mockito.mock(WarehouseService.class);
        mockRepository = Mockito.mock(OrderRepository.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        inOrder = Mockito.inOrder(mockWarehouseService, mockRepository, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new OrderServiceImpl(mockWarehouseService, mockRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockWarehouseService, mockRepository, mockUUIDGenerator);
    }

    @Test
    void whenAddNewOrderAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.addNewOrder(WRONG_USERNAME, getTestShoppingCartA(), getTestAddressA()));

        assertThat(exception.getUserMessage(), equalTo("User not authorized"));
    }

    @Test
    void whenAddNewOrderAndProductNotInWarehouse_ThenThrowException() {
        when(mockWarehouseService.checkProductsAvailability(any()))
                .thenThrow(new ProductInShoppingCartNotInWarehouse(TEST_EXCEPTION_MESSAGE));

        final NoSpecifiedProductInWarehouseException e = assertThrows(NoSpecifiedProductInWarehouseException.class,
                () -> service.addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA()));

        verify(mockWarehouseService).checkProductsAvailability(getTestShoppingCartA());
        assertThat(e.getUserMessage(), equalTo(TEST_EXCEPTION_MESSAGE));
    }

    @Test
    void whenAddNewOrderAndProductLowInWarehouse_ThenThrowException() {
        when(mockWarehouseService.checkProductsAvailability(any()))
                .thenThrow(new ProductInShoppingCartLowQuantityInWarehouse(TEST_EXCEPTION_MESSAGE));

        final NoSpecifiedProductInWarehouseException e = assertThrows(NoSpecifiedProductInWarehouseException.class,
                () -> service.addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA()));

        verify(mockWarehouseService).checkProductsAvailability(getTestShoppingCartA());
        assertThat(e.getUserMessage(), equalTo(TEST_EXCEPTION_MESSAGE));
    }

    @Test
    void whenAddNewOrderAndCorrectUsername_ThenCreateOrderAndSaveItToRepositoryAndReturnItAndLog() throws Exception {
        when(mockWarehouseService.checkProductsAvailability(any())).thenReturn(getTestBookedProductsDto());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(ORDER_ID_A);
        when(mockRepository.save(any())).thenReturn(getTestNewOrder());

        final Order order = service.addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA());

        inOrder.verify(mockWarehouseService).checkProductsAvailability(getTestShoppingCartA());
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestNewOrder())));
        assertThat(order, samePropertyValuesAs(getTestNewOrder()));
        assertLogs(logListener.getEvents(), "add_order.json", getClass());
    }

    @Test
    void whenGetOrderByIdAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.getOrderById(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenFetOrderBYIdAndOrderExist_ThenReturnOrder() {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderA()));

        final Order order = service.getOrderById(ORDER_ID_A);

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(order, samePropertyValuesAs(getTestOrderA()));
    }

    @Test
    void whenFindOrdersByUsernameAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.findOrdersByUsername(WRONG_USERNAME, PAGEABLE));

        assertThat(exception.getUserMessage(), equalTo("User not authorized"));
    }

    @Test
    void whenFindOrdersByUsername_ThenReturnListOfOrders() {
        when(mockRepository.findAllByUsername(any(), any())).thenReturn(List.of(getTestOrderA(), getTestOrderB()));

        final List<Order> orders = service.findOrdersByUsername(USERNAME_A, PAGEABLE);

        verify(mockRepository).findAllByUsername(USERNAME_A, PAGEABLE);
        assertThat(orders, contains(samePropertyValuesAs(getTestOrderA()), samePropertyValuesAs(getTestOrderB())));
    }

    @Test
    void whenConfirmAssemblyAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmAssembly(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenConfirmAssemblyAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderANew()));
        when(mockRepository.save(any())).thenReturn(getTestOrderAAssembled());

        final Order order = service.confirmAssembly(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderAAssembled())));
        assertThat(order, samePropertyValuesAs(getTestOrderAAssembled()));
        assertLogs(logListener.getEvents(), "confirm_assembly.json", getClass());
    }

    @Test
    void whenSetAssemblyFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setAssemblyFailed(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenSetAssemblyFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws  Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderANew()));
        when(mockRepository.save(any())).thenReturn(getTestOrderANotAssembled());

        final Order order = service.setAssemblyFailed(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderANotAssembled())));
        assertThat(order, samePropertyValuesAs(getTestOrderANotAssembled()));
        assertLogs(logListener.getEvents(), "set_assembly_failed.json", getClass());
    }

    @Test
    void whenConfirmPaymentAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmPayment(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenConfirmPaymentAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderAAssembled()));
        when(mockRepository.save(any())).thenReturn(getTestOrderAPaid());

        final Order order = service.confirmPayment(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderAPaid())));
        assertThat(order, samePropertyValuesAs(getTestOrderAPaid()));
        assertLogs(logListener.getEvents(), "confirm_payment.json", getClass());
    }

    @Test
    void whenSetPaymentFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setPaymentFailed(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenSetPaymentFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderAAssembled()));
        when(mockRepository.save(any())).thenReturn(getTestOrderAUnpaid());

        final Order order = service.setPaymentFailed(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderAUnpaid())));
        assertThat(order, samePropertyValuesAs(getTestOrderAUnpaid()));
        assertLogs(logListener.getEvents(), "set_payment_failed.json", getClass());
    }

    @Test
    void whenConfirmDeliveryAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmDelivery(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenConfirmDeliveryAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderAAssembled()));
        when(mockRepository.save(any())).thenReturn(getTestOrderADelivered());

        final Order order = service.confirmDelivery(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderADelivered())));
        assertThat(order, samePropertyValuesAs(getTestOrderADelivered()));
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSetDeliveryFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setDeliveryFailed(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenSetDeliveryFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderAAssembled()));
        when(mockRepository.save(any())).thenReturn(getTestOrderANotDelivered());

        final Order order = service.setDeliveryFailed(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderANotDelivered())));
        assertThat(order, samePropertyValuesAs(getTestOrderANotDelivered()));
        assertLogs(logListener.getEvents(), "set_delivery_failed.json", getClass());
    }

    @Test
    void whenCompleteOrderAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.completeOrder(ORDER_ID_A));

        verify(mockRepository).findById(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenCompleteOrderAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(getTestOrderADelivered()));
        when(mockRepository.save(any())).thenReturn(getTestOrderA());

        final Order order = service.completeOrder(ORDER_ID_A);

        inOrder.verify(mockRepository).findById(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestOrderA())));
        assertThat(order, samePropertyValuesAs(getTestOrderA()));
        assertLogs(logListener.getEvents(), "complete_order.json", getClass());
    }
}