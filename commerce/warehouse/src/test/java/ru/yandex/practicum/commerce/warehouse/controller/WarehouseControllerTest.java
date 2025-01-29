package ru.yandex.practicum.commerce.warehouse.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;
import ru.yandex.practicum.commerce.warehouse.util.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddProductToWarehouseRequest;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestBookedProducts;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestNewProductDto;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestProductA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestShoppingCart;
import static ru.yandex.practicum.commerce.warehouse.util.TestUtils.assertLogs;

class WarehouseControllerTest {

    private static final LogListener logListener = new LogListener(WarehouseController.class);
    private ProductService mockProductService;
    private AddressService mockAddressService;
    private ProductMapper mockProductMapper;
    private InOrder inOrder;

    private WarehouseController controller;

    @BeforeEach
    void setUp() {
        mockProductService = Mockito.mock(ProductService.class);
        mockAddressService = Mockito.mock(AddressService.class);
        mockProductMapper = Mockito.mock(ProductMapper.class);
        inOrder = Mockito.inOrder(mockProductService, mockAddressService, mockProductMapper);
        logListener.startListen();
        logListener.reset();
        controller = new WarehouseController(mockProductService, mockAddressService, mockProductMapper);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockProductService, mockAddressService, mockProductMapper);
    }

    @Test
    void whenAddNewProduct_ThenMapRequestAndPassToProductServiceAndLog() throws Exception {
        when(mockProductMapper.mapToEntity(any())).thenReturn(getTestProductA());
        doNothing().when(mockProductService).addNewProduct(any());

        controller.addNewProduct(getTestNewProductDto());

        inOrder.verify(mockProductMapper).mapToEntity(getTestNewProductDto());
        inOrder.verify(mockProductService).addNewProduct(refEq(getTestProductA()));
        assertLogs(logListener.getEvents(), "add_new_product.json", getClass());
    }

    @Test
    void whenBookProducts_ThenPassShoppingCartToProductServiceAndReturnBookedProductsFromProductServiceAndLog()
            throws Exception {
        when(mockProductService.bookProductsInWarehouse(any())).thenReturn(getTestBookedProducts());

        final BookedProductsDto dto = controller.bookProducts(getTestShoppingCart());

        verify(mockProductService).bookProductsInWarehouse(getTestShoppingCart());
        assertThat(dto, equalTo(getTestBookedProducts()));
        assertLogs(logListener.getEvents(), "book_products.json", getClass());
    }

    @Test
    void whenIncreaseProductQuantity_ThenPassRequestToProductServiceAndLog() throws Exception {
        doNothing().when(mockProductService).increaseProductQuantity(any());

        controller.increaseProductQuantity(getTestAddProductToWarehouseRequest());

        verify(mockProductService).increaseProductQuantity(getTestAddProductToWarehouseRequest());
        assertLogs(logListener.getEvents(), "add_product_quantity.json", getClass());
    }

    @Test
    void whenGetWarehouseAddress_ThenReturnAddressServiceResponseAndLog() throws Exception {
        when(mockAddressService.getAddress()).thenReturn(getTestAddressDtoA());

        final AddressDto dto = controller.getWarehouseAddress();

        verify(mockAddressService).getAddress();
        assertThat(dto, equalTo(getTestAddressDtoA()));
        assertLogs(logListener.getEvents(), "get_address.json", getClass());
    }
}