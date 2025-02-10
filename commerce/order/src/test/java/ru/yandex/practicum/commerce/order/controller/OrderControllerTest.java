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
import static ru.yandex.practicum.commerce.order.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.PAGEABLE;
import static ru.yandex.practicum.commerce.order.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestAddressA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestCreateNewOrderRequest;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestNewOrder;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestNewOrderDto;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderADelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderANotAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderANotDelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAPaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderAUnpaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderB;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoA;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoAAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoADelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoANotAssembled;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoANotDelivered;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoAPaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoAUnpaid;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestOrderDtoB;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestPageable;
import static ru.yandex.practicum.commerce.order.util.TestModels.getTestShoppingCartA;
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
        when(mockAddressMapper.mapToEntity(any())).thenReturn(getTestAddressA());
        when(mockOrderService.addNewOrder(any(), any(), any())).thenReturn(getTestNewOrder());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestNewOrderDto());

        final OrderDto dto = controller.createOrder(USERNAME_A, getTestCreateNewOrderRequest());

        inOrder.verify(mockAddressMapper).mapToEntity(getTestAddressDtoA());
        inOrder.verify(mockOrderService).addNewOrder(USERNAME_A, getTestShoppingCartA(), getTestAddressA());
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestNewOrder()));
        assertThat(dto, equalTo(getTestNewOrderDto()));
        assertLogs(logListener.getEvents(), "create_order.json", getClass());
    }

    @Test
    void whenGetOrdersByUsername_ThenMapPageableAndPassWithUsernameToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockOrderService.findOrdersByUsername(any(), any())).thenReturn(List.of(getTestOrderA(), getTestOrderB()));
        when(mockOrderMapper.mapToDto(anyList())).thenReturn(List.of(getTestOrderDtoA(), getTestOrderDtoB()));

        final List<OrderDto> dtos = controller.getOrdersByUsername(USERNAME_A, getTestPageable());

        inOrder.verify(mockOrderService).findOrdersByUsername(USERNAME_A, PAGEABLE);
        inOrder.verify(mockOrderMapper).mapToDto(ordersCaptor.capture());
        assertThat(ordersCaptor.getValue(), contains(samePropertyValuesAs(getTestOrderA()),
                samePropertyValuesAs(getTestOrderB())));
        assertThat(dtos, contains(getTestOrderDtoA(), getTestOrderDtoB()));
        assertLogs(logListener.getEvents(), "get_orders_by_username.json", getClass());
    }

    @Test
    void whenConfirmAssembly_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmAssembly(any())).thenReturn(getTestOrderAAssembled());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoAAssembled());

        final OrderDto dto = controller.confirmAssembly(ORDER_ID_A);

        inOrder.verify(mockOrderService).confirmAssembly(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderAAssembled()));
        assertThat(dto, equalTo(getTestOrderDtoAAssembled()));
        assertLogs(logListener.getEvents(), "confirm_assembly.json", getClass());
    }

    @Test
    void whenSetAssemblyFailed_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setAssemblyFailed(any())).thenReturn(getTestOrderANotAssembled());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoANotAssembled());

        final OrderDto dto = controller.setAssemblyFailed(ORDER_ID_A);

        inOrder.verify(mockOrderService).setAssemblyFailed(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderANotAssembled()));
        assertThat(dto, equalTo(getTestOrderDtoANotAssembled()));
        assertLogs(logListener.getEvents(), "set_assembly_failed.json", getClass());
    }

    @Test
    void whenConfirmPayment_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmPayment(any())).thenReturn(getTestOrderAPaid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoAPaid());

        final OrderDto dto = controller.confirmPayment(ORDER_ID_A);

        inOrder.verify(mockOrderService).confirmPayment(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderAPaid()));
        assertThat(dto, equalToObject(getTestOrderDtoAPaid()));
        assertLogs(logListener.getEvents(), "confirm_payment.json", getClass());
    }

    @Test
    void whenSetPaymentFailed_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setPaymentFailed(any())).thenReturn(getTestOrderAUnpaid());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoAUnpaid());

        final OrderDto dto = controller.setPaymentFailed(ORDER_ID_A);

        inOrder.verify(mockOrderService).setPaymentFailed(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderAUnpaid()));
        assertThat(dto, equalTo(getTestOrderDtoAUnpaid()));
        assertLogs(logListener.getEvents(), "set_payment_failed.json", getClass());
    }

    @Test
    void whenConfirmDelivery_ThenPassOrderIdToServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.confirmDelivery(any())).thenReturn(getTestOrderADelivered());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoADelivered());

        final OrderDto dto = controller.confirmDelivery(ORDER_ID_A);

        inOrder.verify(mockOrderService).confirmDelivery(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderADelivered()));
        assertThat(dto, equalTo(getTestOrderDtoADelivered()));
        assertLogs(logListener.getEvents(), "confirm_delivery.json", getClass());
    }

    @Test
    void whenSetDeliveryFailed_ThenPassOrderIdTOServiceAndMapServiceResponseAndReturnItAndLog() throws Exception {
        when(mockOrderService.setDeliveryFailed(any())).thenReturn(getTestOrderANotDelivered());
        when(mockOrderMapper.mapToDto(any(Order.class))).thenReturn(getTestOrderDtoANotDelivered());

        final OrderDto dto = controller.setDeliveryFailed(ORDER_ID_A);

        inOrder.verify(mockOrderService).setDeliveryFailed(ORDER_ID_A);
        inOrder.verify(mockOrderMapper).mapToDto(refEq(getTestOrderANotDelivered()));
        assertThat(dto, equalTo(getTestOrderDtoANotDelivered()));
        assertLogs(logListener.getEvents(), "set_delivery_failed.json", getClass());
    }
}