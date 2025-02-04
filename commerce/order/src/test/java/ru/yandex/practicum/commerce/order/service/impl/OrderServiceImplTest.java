package ru.yandex.practicum.commerce.order.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
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
}