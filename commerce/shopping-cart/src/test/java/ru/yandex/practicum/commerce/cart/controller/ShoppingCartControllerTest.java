package ru.yandex.practicum.commerce.cart.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.cart.util.LogListener;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.commerce.cart.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestBookedProductsDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestChangeProductQuantityRequest;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCartDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToAdd;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToDelete;
import static ru.yandex.practicum.commerce.cart.util.TestUtils.assertLogs;

class ShoppingCartControllerTest {

    private static final LogListener logListener = new LogListener(ShoppingCartController.class);
    private ShoppingCartService mockService;
    private ShoppingCartMapper mockMapper;
    private InOrder inOrder;

    private ShoppingCartController controller;

    @BeforeEach
    void setUp() {
        mockService = Mockito.mock(ShoppingCartService.class);
        mockMapper = Mockito.mock(ShoppingCartMapper.class);
        inOrder = Mockito.inOrder(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        controller = new ShoppingCartController(mockService, mockMapper);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
    }

    @Test
    void whenGetShoppingCartByUsername_ThenPassUsernameToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockService.getShoppingCartByUsername(any())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        final ShoppingCartDto dto = controller.getShoppingCartByUsername(USERNAME_A);

        inOrder.verify(mockService).getShoppingCartByUsername(USERNAME_A);
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
        assertThat(dto, equalTo(getTestFullShoppingCartDto()));
        assertLogs(logListener.getEvents(), "get_shopping_cart.json", getClass());
    }

    @Test
    void whenAddProductsToShoppingCart_ThenPassUsernameProductsToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockService.addProductsToShoppingCart(any(), anyMap())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        final ShoppingCartDto dto = controller.addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd());

        inOrder.verify(mockService).addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
        assertThat(dto, equalTo(getTestFullShoppingCartDto()));
        assertLogs(logListener.getEvents(), "add_products.json", getClass());
    }

    @Test
    void whenDeactivateShoppingCartByUsername_ThenPassUsernameToServiceAndLog() throws Exception {
        doNothing().when(mockService).deactivateShoppingCart(any());

        controller.deactivateShoppingCartByUsername(USERNAME_A);

        verify(mockService).deactivateShoppingCart(USERNAME_A);
        assertLogs(logListener.getEvents(), "deactivate_shopping_cart.json", getClass());
    }

    @Test
    void whenDeleteProductsFromShoppingCart_ThenPassUsernameProductsToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockService.deleteProductsFromShoppingCart(any(), anySet())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        final ShoppingCartDto dto = controller.deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete());

        inOrder.verify(mockService).deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
        assertThat(dto, equalTo(getTestFullShoppingCartDto()));
        assertLogs(logListener.getEvents(), "delete_products.json", getClass());
    }

    @Test
    void whenChangeProductQuantity_ThenPassUsernameRequestToServiceAndMapServiceResponseAndReturnItAndLog()
            throws Exception {
        when(mockService.changeProductQuantity(any(), any())).thenReturn(getTestFullShoppingCart());
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());

        final ShoppingCartDto dto = controller.changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest());

        inOrder.verify(mockService).changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest());
        inOrder.verify(mockMapper).mapToDto(refEq(getTestFullShoppingCart()));
        assertThat(dto, equalTo(getTestFullShoppingCartDto()));
        assertLogs(logListener.getEvents(), "change_product_quantity.json", getClass());
    }

    @Test
    void whenBookProductsInWarehouse_ThenPassUsernameToServiceAndReturnServiceResponseAndLog() throws Exception {
        when(mockService.bookProductsInWarehouse(any())).thenReturn(getTestBookedProductsDto());

        final BookedProductsDto dto = controller.bookProductsInWarehouse(USERNAME_A);

        verify(mockService).bookProductsInWarehouse(USERNAME_A);
        assertThat(dto, equalTo(getTestBookedProductsDto()));
        assertLogs(logListener.getEvents(), "book_products_in_warehouse.json", getClass());
    }
}