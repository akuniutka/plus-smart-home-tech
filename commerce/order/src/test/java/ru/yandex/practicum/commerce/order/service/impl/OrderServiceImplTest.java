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
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.client.DeliveryClient;
import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.commerce.order.client.PaymentClient;
import ru.yandex.practicum.commerce.order.client.WarehouseClient;
import ru.yandex.practicum.commerce.order.util.LogListener;
import ru.yandex.practicum.commerce.order.util.TestAddress;
import ru.yandex.practicum.commerce.order.util.TestBookedProductsDto;
import ru.yandex.practicum.commerce.order.util.TestOrder;
import ru.yandex.practicum.commerce.order.util.TestOrderDto;
import ru.yandex.practicum.commerce.order.util.TestProductReturnRequest;
import ru.yandex.practicum.commerce.order.util.TestShoppingCartDto;
import ru.yandex.practicum.commerce.order.util.UUIDGenerator;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.order.util.TestModels.DELIVERY_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.PRODUCT_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.order.util.TestModels.TOTAL_PRICE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME;
import static ru.yandex.practicum.commerce.order.util.TestUtils.assertLogs;

class OrderServiceImplTest {

    private static final LogListener logListener = new LogListener(OrderServiceImpl.class);
    private static final String WRONG_USERNAME = "";
    private WarehouseClient mockWarehouseClient;
    private PaymentClient mockPaymentClient;
    private DeliveryClient mockDeliveryClient;
    private OrderRepository mockRepository;
    private OrderMapper mockOrderMapper;
    private UUIDGenerator mockUUIDGenerator;
    private InOrder inOrder;

    private OrderService service;

    @BeforeEach
    void setUp() {
        mockWarehouseClient = Mockito.mock(WarehouseClient.class);
        mockPaymentClient = Mockito.mock(PaymentClient.class);
        mockDeliveryClient = Mockito.mock(DeliveryClient.class);
        mockRepository = Mockito.mock(OrderRepository.class);
        mockOrderMapper = Mockito.mock(OrderMapper.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        inOrder = Mockito.inOrder(mockWarehouseClient, mockPaymentClient, mockDeliveryClient, mockRepository,
                mockOrderMapper, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new OrderServiceImpl(mockWarehouseClient, mockPaymentClient, mockDeliveryClient, mockRepository,
                mockOrderMapper, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockWarehouseClient, mockPaymentClient, mockDeliveryClient, mockRepository,
                mockOrderMapper, mockUUIDGenerator);
    }

    @Test
    void whenAddNewOrderAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.addNewOrder(WRONG_USERNAME, TestShoppingCartDto.create(), TestAddress.create()));

        assertThat(exception.getUserMessage(), equalTo("User not authorized"));
    }

    @Test
    void whenAddNewOrderAndProductNotInWarehouse_ThenThrowException() {
        when(mockWarehouseClient.checkProductsAvailability(any()))
                .thenThrow(new ProductInShoppingCartNotInWarehouse(TEST_EXCEPTION_MESSAGE));

        final NoSpecifiedProductInWarehouseException e = assertThrows(NoSpecifiedProductInWarehouseException.class,
                () -> service.addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create()));

        verify(mockWarehouseClient).checkProductsAvailability(TestShoppingCartDto.create());
        assertThat(e.getUserMessage(), equalTo(TEST_EXCEPTION_MESSAGE));
    }

    @Test
    void whenAddNewOrderAndProductLowInWarehouse_ThenThrowException() {
        when(mockWarehouseClient.checkProductsAvailability(any()))
                .thenThrow(new ProductInShoppingCartLowQuantityInWarehouse(TEST_EXCEPTION_MESSAGE));

        final NoSpecifiedProductInWarehouseException e = assertThrows(NoSpecifiedProductInWarehouseException.class,
                () -> service.addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create()));

        verify(mockWarehouseClient).checkProductsAvailability(TestShoppingCartDto.create());
        assertThat(e.getUserMessage(), equalTo(TEST_EXCEPTION_MESSAGE));
    }

    @Test
    void whenAddNewOrderAndCorrectUsername_ThenCreateOrderAndSaveItToRepositoryAndReturnItAndLog() throws Exception {
        when(mockWarehouseClient.checkProductsAvailability(any())).thenReturn(TestBookedProductsDto.create());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(ORDER_ID);
        when(mockRepository.save(any())).thenReturn(TestOrder.create());

        final Order order = service.addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create());

        inOrder.verify(mockWarehouseClient).checkProductsAvailability(TestShoppingCartDto.create());
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.create())));
        assertThat(order, samePropertyValuesAs(TestOrder.create()));
        assertLogs(logListener.getEvents(), "add_order.json", getClass());
    }

    @Test
    void whenGetOrderByIdAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.getOrderById(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenGetOrderByIdAndOrderExist_ThenReturnOrder() {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.completed()));

        final Order order = service.getOrderById(ORDER_ID);

        verify(mockRepository).findById(ORDER_ID);
        assertThat(order, samePropertyValuesAs(TestOrder.completed()));
    }

    @Test
    void whenFindOrdersByUsernameAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.findOrdersByUsername(WRONG_USERNAME, PAGEABLE));

        assertThat(exception.getUserMessage(), equalTo("User not authorized"));
    }

    @Test
    void whenFindOrdersByUsername_ThenReturnListOfOrders() {
        when(mockRepository.findAllByUsername(any(), any())).thenReturn(List.of(TestOrder.completed(),
                TestOrder.other()));

        final List<Order> orders = service.findOrdersByUsername(USERNAME, PAGEABLE);

        verify(mockRepository).findAllByUsername(USERNAME, PAGEABLE);
        assertThat(orders, contains(samePropertyValuesAs(TestOrder.completed()),
                samePropertyValuesAs(TestOrder.other())));
    }

    @Test
    void whenCalculateProductCostAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.calculateProductCost(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenCalculateProductCostAnOrderExist_ThenMapOrderToDtoAndPassToPaymentClientAndUpdateOrderAndReturnItAndLog()
            throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.withDeliveryParams()));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenAnswer(invocation -> {
            assertThat(invocation.getArgument(0), samePropertyValuesAs(TestOrder.withDeliveryParams()));
            return TestOrderDto.withDeliveryParams();
        });
        when(mockPaymentClient.calculateProductCost(any())).thenReturn(PRODUCT_PRICE);
        when(mockRepository.save(any())).thenReturn(TestOrder.withProductPrice());

        final Order order = service.calculateProductCost(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(any(Order.class));
        inOrder.verify(mockPaymentClient).calculateProductCost(TestOrderDto.withDeliveryParams());
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withProductPrice())));
        assertThat(order, samePropertyValuesAs(TestOrder.withProductPrice()));
        assertLogs(logListener.getEvents(), "calculate_product_cost.json", getClass());
    }

    @Test
    void whenCalculateDeliveryCostAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.calculateDeliveryCost(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenCalculateDeliveryCostAndOrderExist_ThenMapOrderToDtoAndPassToDeliveryClientAndUpdateOrderAndReturnAndLog()
            throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.withProductPrice()));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenAnswer(invocation -> {
            assertThat(invocation.getArgument(0), samePropertyValuesAs(TestOrder.withProductPrice()));
            return TestOrderDto.withProductPrice();
        });
        when(mockDeliveryClient.calculateDeliveryCost(any())).thenReturn(DELIVERY_PRICE);
        when(mockRepository.save(any())).thenReturn(TestOrder.withDeliveryPrice());

        final Order order = service.calculateDeliveryCost(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(any(Order.class));
        inOrder.verify(mockDeliveryClient).calculateDeliveryCost(TestOrderDto.withProductPrice());
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withDeliveryPrice())));
        assertThat(order, samePropertyValuesAs(TestOrder.withDeliveryPrice()));
        assertLogs(logListener.getEvents(), "calculate_delivery_cost.json", getClass());
    }

    @Test
    void whenCalculateTotalCostAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.calculateTotalCost(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenCalculateTotalCostAndOrderExist_ThenMapOrderToDtoAndPassToPaymentClientAndUpdateOrderAndReturnItAndLog()
            throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.withDeliveryPrice()));
        when(mockOrderMapper.mapToDto(any(Order.class))).thenAnswer(invocation -> {
            assertThat(invocation.getArgument(0), samePropertyValuesAs(TestOrder.withDeliveryPrice()));
            return TestOrderDto.withDeliveryPrice();
        });
        when(mockPaymentClient.calculateTotalCost(any())).thenReturn(TOTAL_PRICE);
        when(mockRepository.save(any())).thenReturn(TestOrder.withTotalPrice());

        final Order order = service.calculateTotalCost(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(any(Order.class));
        inOrder.verify(mockPaymentClient).calculateTotalCost(TestOrderDto.withDeliveryPrice());
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withTotalPrice())));
        assertThat(order, samePropertyValuesAs(TestOrder.withTotalPrice()));
        assertLogs(logListener.getEvents(), "calculate_total_cost.json", getClass());
    }

    @Test
    void whenConfirmAssemblyAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmAssembly(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenConfirmAssemblyAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.withPaymentAndDelivery()));
        when(mockRepository.save(any())).thenReturn(TestOrder.assembled());

        final Order order = service.confirmAssembly(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.assembled())));
        assertThat(order, samePropertyValuesAs(TestOrder.assembled()));
        assertLogs(logListener.getEvents(), "confirm_assembly.json", getClass());
    }

    @Test
    void whenSetAssemblyFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setAssemblyFailed(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenSetAssemblyFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.withPaymentAndDelivery()));
        when(mockRepository.save(any())).thenReturn(TestOrder.withAssemblyFailed());

        final Order order = service.setAssemblyFailed(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withAssemblyFailed())));
        assertThat(order, samePropertyValuesAs(TestOrder.withAssemblyFailed()));
        assertLogs(logListener.getEvents(), "set_assembly_failed.json", getClass());
    }

    @Test
    void whenConfirmPaymentAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmPayment(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenConfirmPaymentAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.assembled()));
        when(mockRepository.save(any())).thenReturn(TestOrder.paid());

        final Order order = service.confirmPayment(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.paid())));
        assertThat(order, samePropertyValuesAs(TestOrder.paid()));
        assertLogs(logListener.getEvents(), "confirm_payment.json", getClass());
    }

    @Test
    void whenSetPaymentFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setPaymentFailed(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenSetPaymentFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.assembled()));
        when(mockRepository.save(any())).thenReturn(TestOrder.withPaymentFailed());

        final Order order = service.setPaymentFailed(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withPaymentFailed())));
        assertThat(order, samePropertyValuesAs(TestOrder.withPaymentFailed()));
        assertLogs(logListener.getEvents(), "set_payment_failed.json", getClass());
    }

    @Test
    void whenConfirmDeliveryAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.confirmDelivery(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenConfirmDeliveryAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.paid()));
        when(mockRepository.save(any())).thenReturn(TestOrder.delivered());

        final Order order = service.confirmDelivery(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.delivered())));
        assertThat(order, samePropertyValuesAs(TestOrder.delivered()));
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSetDeliveryFailedAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.setDeliveryFailed(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenSetDeliveryFailedAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.paid()));
        when(mockRepository.save(any())).thenReturn(TestOrder.withDeliveryFailed());

        final Order order = service.setDeliveryFailed(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.withDeliveryFailed())));
        assertThat(order, samePropertyValuesAs(TestOrder.withDeliveryFailed()));
        assertLogs(logListener.getEvents(), "set_delivery_failed.json", getClass());
    }

    @Test
    void whenReturnProductsAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.returnProducts(TestProductReturnRequest.create()));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenReturnProductsAndOrderExist_ThenPassProductsToWarehouseClientAndUpdateOrderAndReturnItAndLog()
            throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.paid()));
        doNothing().when(mockWarehouseClient).returnProducts(any());
        when(mockRepository.save(any())).thenReturn(TestOrder.returned());

        final Order order = service.returnProducts(TestProductReturnRequest.create());

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockWarehouseClient).returnProducts(TestShoppingCartDto.PRODUCTS);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.returned())));
        assertThat(order, samePropertyValuesAs(TestOrder.returned()));
        assertLogs(logListener.getEvents(), "return_products.json", getClass());
    }

    @Test
    void whenCompleteOrderAndOrderNotExist_ThenThrowException() {
        when(mockRepository.findById(any())).thenReturn(Optional.empty());

        final NoOrderFoundException exception = assertThrows(NoOrderFoundException.class,
                () -> service.completeOrder(ORDER_ID));

        verify(mockRepository).findById(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenCompleteOrderAndOrderExist_ThenUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findById(any())).thenReturn(Optional.of(TestOrder.delivered()));
        when(mockRepository.save(any())).thenReturn(TestOrder.completed());

        final Order order = service.completeOrder(ORDER_ID);

        inOrder.verify(mockRepository).findById(ORDER_ID);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(TestOrder.completed())));
        assertThat(order, samePropertyValuesAs(TestOrder.completed()));
        assertLogs(logListener.getEvents(), "complete_order.json", getClass());
    }
}