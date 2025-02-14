package ru.yandex.practicum.commerce.order.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.order.mapper.AddressMapper;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.commerce.order.util.LogListener;
import ru.yandex.practicum.commerce.order.util.TestAddress;
import ru.yandex.practicum.commerce.order.util.TestAddressDto;
import ru.yandex.practicum.commerce.order.util.TestCreateNewOrderRequest;
import ru.yandex.practicum.commerce.order.util.TestOrder;
import ru.yandex.practicum.commerce.order.util.TestOrderDto;
import ru.yandex.practicum.commerce.order.util.TestPageable;
import ru.yandex.practicum.commerce.order.util.TestProductReturnRequest;
import ru.yandex.practicum.commerce.order.util.TestShoppingCartDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToObject;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME;
import static ru.yandex.practicum.commerce.order.util.TestUtils.assertLogs;

class OrderControllerTest {

    private static final LogListener logListener = new LogListener(OrderController.class);
    private AutoCloseable openMocks;

    @Mock
    private OrderService mockOrderService;

    @Mock
    private OrderMapper mockOrderMapper;

    @Captor
    private ArgumentCaptor<List<Order>> ordersCaptor;

    @Mock
    private AddressMapper mockAddressMapper;

    private InOrder inOrder;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        inOrder = Mockito.inOrder(mockOrderService, mockOrderMapper, mockAddressMapper);
        logListener.startListen();
        logListener.reset();
        controller = new OrderController(mockOrderService, mockOrderMapper, mockAddressMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockOrderService, mockOrderMapper, mockAddressMapper);
        openMocks.close();
    }

    @Test
    void whenCreateOrder_ThenMapAddressAndPassUsernameCartAddressToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockAddressMapper.mapToEntity(any())).thenReturn(TestAddress.create());
        when(mockOrderService.addNewOrder(any(), any(), any())).thenReturn(TestOrder.create());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.create());

        final OrderDto dto = controller.createOrder(USERNAME, TestCreateNewOrderRequest.create());

        inOrder.verify(mockAddressMapper).mapToEntity(TestAddressDto.create());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME, TestShoppingCartDto.create(), TestAddress.create());
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.create()));
        assertThat(dto, equalTo(TestOrderDto.create()));
        assertLogs(logListener.getEvents(), "create_order.json", getClass());
    }

    @Test
    void whenGetOrderById_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.getOrderById(any())).thenReturn(TestOrder.completed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.completed());

        final OrderDto dto = controller.getOrderById(ORDER_ID);

        inOrder.verify(mockOrderService).getOrderById(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.completed()));
        assertThat(dto, equalTo(TestOrderDto.completed()));
        assertLogs(logListener.getEvents(), "get_order_by_id.json", getClass());
    }

    @Test
    void whenGetOrdersByUsername_ThenMapPageableAndPassWithUsernameToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockOrderService.findOrdersByUsername(any(), any())).thenReturn(List.of(TestOrder.completed(),
                TestOrder.other()));
        when(mockOrderMapper.mapToDto(anyList())).thenReturn(List.of(TestOrderDto.completed(), TestOrderDto.other()));

        final List<OrderDto> dtos = controller.getOrdersByUsername(USERNAME, TestPageable.create());

        inOrder.verify(mockOrderService).findOrdersByUsername(USERNAME, PAGEABLE);
        inOrder.verify(mockOrderMapper).mapToDto(ordersCaptor.capture());
        assertThat(ordersCaptor.getValue(), contains(samePropertyValuesAs(TestOrder.completed()),
                samePropertyValuesAs(TestOrder.other())));
        assertThat(dtos, contains(TestOrderDto.completed(), TestOrderDto.other()));
        assertLogs(logListener.getEvents(), "get_orders_by_username.json", getClass());
    }

    @Test
    void whenCalculateProductCost_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.calculateProductCost(any())).thenReturn(TestOrder.withProductPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withProductPrice());

        final OrderDto dto = controller.calculateProductCost(ORDER_ID);

        inOrder.verify(mockOrderService).calculateProductCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withProductPrice()));
        assertThat(dto, equalTo(TestOrderDto.withProductPrice()));
        assertLogs(logListener.getEvents(), "calculate_product_cost.json", getClass());
    }

    @Test
    void whenCalculateDeliveryCost_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.calculateDeliveryCost(any())).thenReturn(TestOrder.withDeliveryPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withDeliveryPrice());

        final OrderDto dto = controller.calculateDeliveryCost(ORDER_ID);

        inOrder.verify(mockOrderService).calculateDeliveryCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withDeliveryPrice()));
        assertThat(dto, equalTo(TestOrderDto.withDeliveryPrice()));
        assertLogs(logListener.getEvents(), "calculate_delivery_cost.json", getClass());
    }

    @Test
    void whenCalculateTotalCost_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.calculateTotalCost(any())).thenReturn(TestOrder.withTotalPrice());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withTotalPrice());

        final OrderDto dto = controller.calculateTotalCost(ORDER_ID);

        inOrder.verify(mockOrderService).calculateTotalCost(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withTotalPrice()));
        assertThat(dto, equalTo(TestOrderDto.withTotalPrice()));
        assertLogs(logListener.getEvents(), "calculate_total_cost.json", getClass());
    }

    @Test
    void whenConfirmAssembly_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmAssembly(any())).thenReturn(TestOrder.assembled());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.assembled());

        final OrderDto dto = controller.confirmAssembly(ORDER_ID);

        inOrder.verify(mockOrderService).confirmAssembly(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.assembled()));
        assertThat(dto, equalTo(TestOrderDto.assembled()));
        assertLogs(logListener.getEvents(), "confirm_assembly.json", getClass());
    }

    @Test
    void whenSetAssemblyFailed_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setAssemblyFailed(any())).thenReturn(TestOrder.withAssemblyFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withAssemblyFailed());

        final OrderDto dto = controller.setAssemblyFailed(ORDER_ID);

        inOrder.verify(mockOrderService).setAssemblyFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withAssemblyFailed()));
        assertThat(dto, equalTo(TestOrderDto.withAssemblyFailed()));
        assertLogs(logListener.getEvents(), "set_assembly_failed.json", getClass());
    }

    @Test
    void whenConfirmPayment_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmPayment(any())).thenReturn(TestOrder.paid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.paid());

        final OrderDto dto = controller.confirmPayment(ORDER_ID);

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.paid()));
        assertThat(dto, equalToObject(TestOrderDto.paid()));
        assertLogs(logListener.getEvents(), "confirm_payment.json", getClass());
    }

    @Test
    void whenSetPaymentFailed_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setPaymentFailed(any())).thenReturn(TestOrder.withPaymentFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withPaymentFailed());

        final OrderDto dto = controller.setPaymentFailed(ORDER_ID);

        inOrder.verify(mockOrderService).setPaymentFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withPaymentFailed()));
        assertThat(dto, equalTo(TestOrderDto.withPaymentFailed()));
        assertLogs(logListener.getEvents(), "set_payment_failed.json", getClass());
    }

    @Test
    void whenConfirmDelivery_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmDelivery(any())).thenReturn(TestOrder.delivered());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.delivered());

        final OrderDto dto = controller.confirmDelivery(ORDER_ID);

        inOrder.verify(mockOrderService).confirmDelivery(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.delivered()));
        assertThat(dto, equalTo(TestOrderDto.delivered()));
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSetDeliveryFailed_ThenPassOrderIdTOServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setDeliveryFailed(any())).thenReturn(TestOrder.withDeliveryFailed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.withDeliveryFailed());

        final OrderDto dto = controller.setDeliveryFailed(ORDER_ID);

        inOrder.verify(mockOrderService).setDeliveryFailed(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.withDeliveryFailed()));
        assertThat(dto, equalTo(TestOrderDto.withDeliveryFailed()));
        assertLogs(logListener.getEvents(), "set_delivery_failed.json", getClass());
    }

    @Test
    void whenReturnProducts_ThenPassProductReturnRequestToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockOrderService.returnProducts(any())).thenReturn(TestOrder.returned());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.returned());

        final OrderDto dto = controller.returnProducts(TestProductReturnRequest.create());

        inOrder.verify(mockOrderService).returnProducts(TestProductReturnRequest.create());
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.returned()));
        assertThat(dto, equalTo(TestOrderDto.returned()));
        assertLogs(logListener.getEvents(), "return_products.json", getClass());
    }

    @Test
    void whenCompleteOrder_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.completeOrder(any())).thenReturn(TestOrder.completed());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(TestOrderDto.completed());

        final OrderDto dto = controller.completeOrder(ORDER_ID);

        inOrder.verify(mockOrderService).completeOrder(ORDER_ID);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(TestOrder.completed()));
        assertThat(dto, equalTo(TestOrderDto.completed()));
        assertLogs(logListener.getEvents(), "complete_order.json", getClass());
    }
}