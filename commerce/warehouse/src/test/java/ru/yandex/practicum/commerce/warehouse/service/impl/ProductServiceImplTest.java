package ru.yandex.practicum.commerce.warehouse.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.commerce.exception.NoOrderBookingFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;
import ru.yandex.practicum.commerce.warehouse.util.LogListener;
import ru.yandex.practicum.commerce.warehouse.util.UUIDGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.ORDER_BOOKING_ID;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.ORDER_ID;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_A;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.PRODUCT_ID_B;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddProductToWarehouseRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAssemblyProductsForOrderRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestDeliveryParams;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestOrderBookingNew;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestOrderBookingWithDeliveryId;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductADecreased;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductAIncreased;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductB;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductBDecreased;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductBLow;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestReturnedProducts;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShippedToDeliveryRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShoppingCart;
import static ru.yandex.practicum.commerce.warehouse.util.TestUtils.assertLogs;

class ProductServiceImplTest {

    private static final LogListener logListener = new LogListener(ProductServiceImpl.class);
    private AutoCloseable openMocks;
    private InOrder inOrder;

    @Mock
    private ProductRepository mockProductRepository;

    @Mock
    private OrderBookingRepository mockOrderBookingRepository;

    @Mock
    private UUIDGenerator mockUUIDGenerator;

    @Captor
    private ArgumentCaptor<Set<UUID>> uuidSetCaptor;

    @Captor
    private ArgumentCaptor<Collection<Product>> productCollectionCaptor;

    private ProductService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        inOrder = Mockito.inOrder(mockProductRepository, mockOrderBookingRepository, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new ProductServiceImpl(mockProductRepository, mockOrderBookingRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockProductRepository, mockOrderBookingRepository, mockUUIDGenerator);
        openMocks.close();
    }

    @Test
    void whenAddNewProductAndProductExist_ThenThrowException() {
        when(mockProductRepository.existsById(any())).thenReturn(true);

        final SpecifiedProductAlreadyInWarehouseException exception = assertThrows(
                SpecifiedProductAlreadyInWarehouseException.class,
                () -> service.addNewProduct(getTestProductA())
        );

        verify(mockProductRepository).existsById(any());
        assertThat(exception.getUserMessage(), equalTo("Product with productId " + PRODUCT_ID_A + " already exists"));
    }

    @Test
    void whenAddNewProductAndProductNotExist_ThenPassItToRepositoryAndLog() throws Exception {
        when(mockProductRepository.existsById(any())).thenReturn(false);
        when(mockProductRepository.save(any())).thenReturn(getTestProductA());

        service.addNewProduct(getTestProductA());

        inOrder.verify(mockProductRepository).existsById(PRODUCT_ID_A);
        inOrder.verify(mockProductRepository).save(argThat(samePropertyValuesAs(getTestProductA())));
        assertLogs(logListener.getEvents(), "add_new_product.json", getClass());
    }

    @Test
    void whenIncreaseProductQuantityAndProductNotExist_ThenThrowException() {
        when(mockProductRepository.findById(any())).thenReturn(Optional.empty());

        final NoSpecifiedProductInWarehouseException exception = assertThrows(
                NoSpecifiedProductInWarehouseException.class,
                () -> service.increaseProductQuantity(getTestAddProductToWarehouseRequest()));

        verify(mockProductRepository).findById(PRODUCT_ID_A);
        assertThat(exception.getUserMessage(), equalTo("Product with productId " + PRODUCT_ID_A + " does not exist"));
    }

    @Test
    void whenIncreaseProductQuantityAndProductExist_ThenIncreaseProductQuantityAndLog() throws Exception {
        when(mockProductRepository.findById(any())).thenReturn(Optional.of(getTestProductA()));
        when(mockProductRepository.save(any())).thenReturn(getTestProductAIncreased());

        service.increaseProductQuantity(getTestAddProductToWarehouseRequest());

        inOrder.verify(mockProductRepository).findById(PRODUCT_ID_A);
        inOrder.verify(mockProductRepository).save(argThat(samePropertyValuesAs(getTestProductAIncreased())));
        assertLogs(logListener.getEvents(), "increase_product_quantity.json", getClass());
    }

    @Test
    void whenCheckProductsAvailabilityAndProductNotExist_ThenThrowException() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductB()));

        final ProductInShoppingCartNotInWarehouse exception = assertThrows(ProductInShoppingCartNotInWarehouse.class,
                () -> service.checkProductsAvailability(getTestShoppingCart()));

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Products not found in warehouse: " + PRODUCT_ID_A));
    }

    @Test
    void whenCheckProductsAvailabilityAndProductNotSuffice_ThenThrowException() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductBLow()));

        final ProductInShoppingCartLowQuantityInWarehouse exception = assertThrows(
                ProductInShoppingCartLowQuantityInWarehouse.class,
                () -> service.checkProductsAvailability(getTestShoppingCart())
        );

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Insufficient stocks in warehouse: " + PRODUCT_ID_B));
    }

    @Test
    void whenCheckProductsAvailabilityAndProductSuffice_ThenReturnDeliveryParameters() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductB()));

        final DeliveryParams deliveryParams = service.checkProductsAvailability(getTestShoppingCart());

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(deliveryParams, equalTo(getTestDeliveryParams()));
    }

    @Test
    void whenBookProductsAndProductNotExist_ThenThrowException() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductB()));

        final ProductInShoppingCartNotInWarehouse exception = assertThrows(ProductInShoppingCartNotInWarehouse.class,
                () -> service.bookProducts(getTestAssemblyProductsForOrderRequest()));

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Products not found in warehouse: " + PRODUCT_ID_A));
    }

    @Test
    void whenBookProductsAndProductNoSuffice_ThenThrowException() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductBLow()));

        final ProductInShoppingCartLowQuantityInWarehouse exception = assertThrows(
                ProductInShoppingCartLowQuantityInWarehouse.class,
                () -> service.bookProducts(getTestAssemblyProductsForOrderRequest())
        );

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Insufficient stocks in warehouse: " + PRODUCT_ID_B));
    }

    @Test
    void whenBookProductsAndProductSuffice_ThenDecreaseStocksAndCreateBookingAndReturnDeliveryParametersAndLog()
            throws Exception {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductA(), getTestProductB()));
        when(mockProductRepository.saveAll(anyCollection())).thenReturn(List.of(getTestProductADecreased(),
                getTestProductBDecreased()));
        when(mockUUIDGenerator.getNewUUID()).thenReturn(ORDER_BOOKING_ID);
        when(mockOrderBookingRepository.save(any())).thenReturn(getTestOrderBookingWithDeliveryId());

        final DeliveryParams deliveryParams = service.bookProducts(getTestAssemblyProductsForOrderRequest());

        inOrder.verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        inOrder.verify(mockProductRepository).saveAll(productCollectionCaptor.capture());
        assertThat(productCollectionCaptor.getValue(), containsInAnyOrder(
                samePropertyValuesAs(getTestProductADecreased()),
                samePropertyValuesAs(getTestProductBDecreased())
        ));
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockOrderBookingRepository).save(argThat(samePropertyValuesAs(getTestOrderBookingNew())));
        assertThat(deliveryParams, equalTo(getTestDeliveryParams()));
        assertLogs(logListener.getEvents(), "book_products.json", getClass());
    }

    @Test
    void whenShippedToDeliveryAndOrderBookingNotExist_ThenThrowException() {
        when(mockOrderBookingRepository.findByOrderId(any())).thenReturn(Optional.empty());

        final NoOrderBookingFoundException exception = assertThrows(NoOrderBookingFoundException.class,
                () -> service.shippedToDelivery(getTestShippedToDeliveryRequest()));

        verify(mockOrderBookingRepository).findByOrderId(ORDER_ID);
        assertThat(exception.getUserMessage(), equalTo("Booking for order " + ORDER_ID + " does not exist"));
    }

    @Test
    void whenShippedToDeliveryAndOrderBookingExist_ThenAddDeliveryIdToOrderBookingAndLog() throws Exception {
        when(mockOrderBookingRepository.findByOrderId(any())).thenReturn(Optional.of(getTestOrderBookingNew()));
        when(mockOrderBookingRepository.save(any())).thenReturn(getTestOrderBookingWithDeliveryId());

        service.shippedToDelivery(getTestShippedToDeliveryRequest());

        inOrder.verify(mockOrderBookingRepository).findByOrderId(ORDER_ID);
        inOrder.verify(mockOrderBookingRepository)
                .save(argThat(samePropertyValuesAs(getTestOrderBookingWithDeliveryId())));
        assertLogs(logListener.getEvents(), "shipped_to_delivery.json", getClass());
    }

    @Test
    void whenReturnProductsAndProductNotExist_ThenThrowException() {
        when(mockProductRepository.findAllById(anySet())).thenReturn(List.of(getTestProductBDecreased()));

        final ProductInShoppingCartNotInWarehouse exception = assertThrows(ProductInShoppingCartNotInWarehouse.class,
                () -> service.returnProducts(getTestReturnedProducts()));

        verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        assertThat(exception.getUserMessage(), equalTo("Products not found in warehouse: " + PRODUCT_ID_A));
    }

    @Test
    void whenReturnProductsAndProductExist_ThenIncreaseStocksAndLog() throws Exception {
        when(mockProductRepository.findAllById(anySet()))
                .thenReturn(List.of(getTestProductADecreased(), getTestProductBDecreased()));
        when(mockProductRepository.saveAll(anyCollection())).thenReturn(List.of(getTestProductA(), getTestProductB()));

        service.returnProducts(getTestReturnedProducts());

        inOrder.verify(mockProductRepository).findAllById(uuidSetCaptor.capture());
        assertThat(uuidSetCaptor.getValue(), containsInAnyOrder(PRODUCT_ID_A, PRODUCT_ID_B));
        inOrder.verify(mockProductRepository).saveAll(productCollectionCaptor.capture());
        assertThat(productCollectionCaptor.getValue(), containsInAnyOrder(
                samePropertyValuesAs(getTestProductA()),
                samePropertyValuesAs(getTestProductB())
        ));
        assertLogs(logListener.getEvents(), "return_products.json", getClass());
    }
}