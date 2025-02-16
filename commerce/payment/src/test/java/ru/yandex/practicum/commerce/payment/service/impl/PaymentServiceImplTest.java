package ru.yandex.practicum.commerce.payment.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoPaymentFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.exception.OrderPaymentAlreadyExistsException;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.commerce.payment.client.OrderClient;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.commerce.payment.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.payment.util.LogListener;
import ru.yandex.practicum.commerce.payment.util.UUIDGenerator;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.payment.util.TestModels.DELIVERY_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.ORDER_ID_A;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PAYMENT_ID;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PRODUCT_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.payment.util.TestModels.PRODUCT_ID_B;
import static ru.yandex.practicum.commerce.payment.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.payment.util.TestModels.TOTAL_COST;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDto;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoNotPaid;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoPaid;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutCosts;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestOrderDtoWithoutTotalCost;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPayment;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPaymentFailed;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestPaymentSuccessful;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestProductDtoA;
import static ru.yandex.practicum.commerce.payment.util.TestModels.getTestProductDtoB;
import static ru.yandex.practicum.commerce.payment.util.TestUtils.assertLogs;

class PaymentServiceImplTest {

    private static final LogListener logListener = new LogListener(PaymentServiceImpl.class);
    private ShoppingStoreClient mockShoppingStoreClient;
    private OrderClient mockOrderClient;
    private PaymentRepository mockRepository;
    private UUIDGenerator mockUUIDGenerator;
    private InOrder inOrder;

    private PaymentService service;

    @BeforeEach
    void setUp() {
        mockShoppingStoreClient = Mockito.mock(ShoppingStoreClient.class);
        mockOrderClient = Mockito.mock(OrderClient.class);
        mockRepository = Mockito.mock(PaymentRepository.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        inOrder = Mockito.inOrder(mockShoppingStoreClient, mockOrderClient, mockRepository, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new PaymentServiceImpl(mockShoppingStoreClient, mockOrderClient, mockRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockShoppingStoreClient, mockOrderClient, mockRepository, mockUUIDGenerator);
    }

    @Test
    void whenCreatePaymentAndPaymentForOrderAlreadyExist_ThenThrowException() {
        when(mockRepository.existsByOrderId(any())).thenReturn(true);

        final OrderPaymentAlreadyExistsException exception = assertThrows(OrderPaymentAlreadyExistsException.class,
                () -> service.createPayment(getTestOrderDto()));

        verify(mockRepository).existsByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Payment for order " + ORDER_ID_A + " already exists"));
    }

    @Test
    void whenCreatePaymentAndPaymentForOrderNotExist_ThenCreatePaymentAndSaveItAndReturnItANdLog() throws Exception {
        when(mockRepository.existsByOrderId(any())).thenReturn(false);
        when(mockUUIDGenerator.getNewUUID()).thenReturn(PAYMENT_ID);
        when(mockRepository.save(any())).thenReturn(getTestPayment());

        final Payment payment = service.createPayment(getTestOrderDto());

        inOrder.verify(mockRepository).existsByOrderId(ORDER_ID_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestPayment())));
        assertThat(payment, samePropertyValuesAs(getTestPayment()));
        assertLogs(logListener.getEvents(), "create_payment.json", getClass());
    }

    @Test
    void whenCalculateProductCostAndProductNotFound_ThenThrowException() {
        final ArgumentCaptor<UUID> productIdCaptor = ArgumentCaptor.forClass(UUID.class);
        when(mockShoppingStoreClient.getProductById(any()))
                .thenReturn(getTestProductDtoA())
                .thenThrow(new ProductNotFoundException(TEST_EXCEPTION_MESSAGE));

        final NotEnoughInfoInOrderToCalculateException exception = assertThrows(
                NotEnoughInfoInOrderToCalculateException.class,
                () -> service.calculateProductCost(getTestOrderDtoWithoutCosts()));

        verify(mockShoppingStoreClient, times(2)).getProductById(productIdCaptor.capture());
        assertThat(productIdCaptor.getAllValues(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Price not found for product " + productIdCaptor.getValue()));
    }

    @Test
    void whenCalculateProductCostAndAllProductsExist_ThenReturnCorrectTotalProductCost() {
        when(mockShoppingStoreClient.getProductById(PRODUCT_ID_A)).thenReturn(getTestProductDtoA());
        when(mockShoppingStoreClient.getProductById(PRODUCT_ID_B)).thenReturn(getTestProductDtoB());

        final BigDecimal productCost = service.calculateProductCost(getTestOrderDtoWithoutCosts());

        verify(mockShoppingStoreClient).getProductById(PRODUCT_ID_A);
        verify(mockShoppingStoreClient).getProductById(PRODUCT_ID_B);
        assertThat(productCost, equalTo(PRODUCT_COST));
    }

    @Test
    void whenCalculateTotalCostAndProductCostNotSet_ThenThrowException() {
        final OrderDto order = getTestOrderDtoWithoutCosts();
        order.setDeliveryPrice(DELIVERY_COST);

        final NotEnoughInfoInOrderToCalculateException exception = assertThrows(
                NotEnoughInfoInOrderToCalculateException.class,
                () -> service.calculateTotalCost(order));

        assertThat(exception.getUserMessage(), equalTo("Product total not set for order " + ORDER_ID_A));
    }

    @Test
    void whenCalculateTotalCostAndDeliveryCostNotSet_ThenThrowException() {
        final OrderDto order = getTestOrderDtoWithoutCosts();
        order.setProductPrice(PRODUCT_COST);

        final NotEnoughInfoInOrderToCalculateException exception = assertThrows(
                NotEnoughInfoInOrderToCalculateException.class,
                () -> service.calculateTotalCost(order));

        assertThat(exception.getUserMessage(), equalTo("Delivery cost not set for order " + ORDER_ID_A));
    }

    @Test
    void whenCalculateTotalCostAndProductCostSetAndDeliveryCostSet_ThenReturnCorrectTotalCost() {

         final BigDecimal totalCost = service.calculateTotalCost(getTestOrderDtoWithoutTotalCost());

         assertThat(totalCost, equalTo(TOTAL_COST));
    }

    @Test
    void whenConfirmPaymentAndPaymentForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoPaymentFoundException exception = assertThrows(NoPaymentFoundException.class,
                () -> service.confirmPayment(ORDER_ID_A));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Payment for order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenConfirmPaymentAndPaymentForOrderExist_ThenUpdatePaymentStateAndUpdateOrderStateAndLog() throws Exception {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(getTestPayment()));
        when(mockRepository.save(any())).thenReturn(getTestPaymentSuccessful());
        when(mockOrderClient.confirmPayment(any())).thenReturn(getTestOrderDtoPaid());

        service.confirmPayment(ORDER_ID_A);

        inOrder.verify(mockRepository).findByOrderId(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestPaymentSuccessful())));
        inOrder.verify(mockOrderClient).confirmPayment(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "confirm_payment.json", getClass());
    }

    @Test
    void whenSignalPaymentFailureAndPaymentForOrderNotExist_ThenThrowException() {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoPaymentFoundException exception = assertThrows(NoPaymentFoundException.class,
                () -> service.signalPaymentFailure(ORDER_ID_A));

        verify(mockRepository).findByOrderId(ORDER_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Payment for order " + ORDER_ID_A + " does not exist"));
    }

    @Test
    void whenSignalPaymentFailureAndPaymentForOrderExist_ThenUpdatePaymentStateAndUpdateOrderStateAndLog()
            throws Exception {
        when(mockRepository.findByOrderId(any())).thenReturn(Optional.of(getTestPayment()));
        when(mockRepository.save(any())).thenReturn(getTestPaymentFailed());
        when(mockOrderClient.setPaymentFailed(any())).thenReturn(getTestOrderDtoNotPaid());

        service.signalPaymentFailure(ORDER_ID_A);

        inOrder.verify(mockRepository).findByOrderId(ORDER_ID_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestPaymentFailed())));
        inOrder.verify(mockOrderClient).setPaymentFailed(ORDER_ID_A);
        assertLogs(logListener.getEvents(), "signal_payment_failure.json", getClass());
    }
}