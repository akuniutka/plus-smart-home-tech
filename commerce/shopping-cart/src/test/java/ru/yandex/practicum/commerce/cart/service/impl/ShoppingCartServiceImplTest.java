package ru.yandex.practicum.commerce.cart.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.cart.exception.BookingNotPossibleException;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.cart.service.WarehouseService;
import ru.yandex.practicum.commerce.cart.util.LogListener;
import ru.yandex.practicum.commerce.cart.util.UUIDGenerator;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.ShoppingCartDeactivatedException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static ru.yandex.practicum.commerce.cart.util.TestModels.PRODUCT_ID_B;
import static ru.yandex.practicum.commerce.cart.util.TestModels.PRODUCT_ID_C;
import static ru.yandex.practicum.commerce.cart.util.TestModels.SHOPPING_CART_ID;
import static ru.yandex.practicum.commerce.cart.util.TestModels.TEST_EXCEPTION_MESSAGE;
import static ru.yandex.practicum.commerce.cart.util.TestModels.USERNAME_A;
import static ru.yandex.practicum.commerce.cart.util.TestModels.WRONG_USERNAME;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestBookedProductsDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestChangeProductQuantityRequest;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestDeactivatedShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestEmptyDeactivatedShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestEmptyShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCartDto;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestFullShoppingCartWithNewQuantity;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestSingleProductShoppingCart;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestThreeProductsToAdd;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToAdd;
import static ru.yandex.practicum.commerce.cart.util.TestModels.getTestTwoProductsToDelete;
import static ru.yandex.practicum.commerce.cart.util.TestUtils.assertLogs;

class ShoppingCartServiceImplTest {

    private static final LogListener logListener = new LogListener(ShoppingCartServiceImpl.class);
    private WarehouseService mockWarehouseService;
    private ShoppingCartMapper mockMapper;
    private ShoppingCartRepository mockRepository;
    private UUIDGenerator mockUUIDGenerator;
    private InOrder inOrder;

    private ShoppingCartService service;

    @BeforeEach
    void setUp() {
        mockWarehouseService = Mockito.mock(WarehouseService.class);
        mockMapper = Mockito.mock(ShoppingCartMapper.class);
        mockRepository = Mockito.mock(ShoppingCartRepository.class);
        mockUUIDGenerator = Mockito.mock(UUIDGenerator.class);
        inOrder = Mockito.inOrder(mockWarehouseService, mockMapper, mockRepository, mockUUIDGenerator);
        logListener.startListen();
        logListener.reset();
        service = new ShoppingCartServiceImpl(mockWarehouseService, mockMapper, mockRepository, mockUUIDGenerator);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockWarehouseService, mockMapper, mockRepository, mockUUIDGenerator);
    }

    @Test
    void whenGetShoppingCartByUsernameAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.getShoppingCartByUsername(WRONG_USERNAME));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenGetShoppingCartByUsernameAndShoppingCartNotExist_ThenCreateItAndReturnAndLog() throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());

        final ShoppingCart shoppingCart = service.getShoppingCartByUsername(USERNAME_A);

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        assertThat(shoppingCart, samePropertyValuesAs(getTestEmptyShoppingCart()));
        assertLogs(logListener.getEvents(), "get_cart_not_exist.json", getClass());
    }

    @Test
    void whenGetShoppingCartByUsernameAndShoppingCartExist_ThenReturnIt() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));

        final ShoppingCart shoppingCart = service.getShoppingCartByUsername(USERNAME_A);

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(shoppingCart, samePropertyValuesAs(getTestFullShoppingCart()));
    }

    @Test
    void whenAddProductsToShoppingCartAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.addProductsToShoppingCart(WRONG_USERNAME, getTestTwoProductsToAdd()));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenAddProductsToShoppingCartAndShoppingCartNotExist_ThenCreateItAndAddProductsAndReturnShoppingCartAndLog()
            throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());
        when(mockRepository.save(any())).thenReturn(getTestFullShoppingCart());

        final ShoppingCart shoppingCart = service.addProductsToShoppingCart(USERNAME_A, getTestThreeProductsToAdd());

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestFullShoppingCart())));
        assertThat(shoppingCart, samePropertyValuesAs(getTestFullShoppingCart()));
        assertLogs(logListener.getEvents(), "add_products_new_cart.json", getClass());
    }

    @Test
    void whenAddProductsToShoppingCartAndShoppingCartDeactivated_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestDeactivatedShoppingCart()));

        final ShoppingCartDeactivatedException exception = assertThrows(ShoppingCartDeactivatedException.class,
                () -> service.addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd()));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("User not authorized to modify shopping cart"));
    }

    @Test
    void whenAddProductsToShoppingCartAndShoppingCartActive_ThenAddProductsAndReturnShoppingCartAndLog()
            throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestSingleProductShoppingCart()));
        when(mockRepository.save(any())).thenReturn(getTestFullShoppingCart());

        final ShoppingCart shoppingCart = service.addProductsToShoppingCart(USERNAME_A, getTestTwoProductsToAdd());

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestFullShoppingCart())));
        assertThat(shoppingCart, samePropertyValuesAs(getTestFullShoppingCart()));
        assertLogs(logListener.getEvents(), "add_products.json", getClass());
    }

    @Test
    void whenDeactivateShoppingCartByUsernameAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.deactivateShoppingCart(WRONG_USERNAME));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenDeactivateShoppingCartByUsernameAndShoppingCartNotExist_ThenCreateItAndDeactivateAndLog()
            throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());
        when(mockRepository.save(any())).thenReturn(getTestEmptyDeactivatedShoppingCart());

        service.deactivateShoppingCart(USERNAME_A);

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyDeactivatedShoppingCart())));
        assertLogs(logListener.getEvents(), "deactivate_new_cart.json", getClass());
    }

    @Test
    void whenDeactivateShoppingCartByUsernameAndShoppingCartExist_ThenDeactivateItAndLog() throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));
        when(mockRepository.save(any())).thenReturn(getTestDeactivatedShoppingCart());

        service.deactivateShoppingCart(USERNAME_A);

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestDeactivatedShoppingCart())));
        assertLogs(logListener.getEvents(), "deactivate_cart.json", getClass());
    }

    @Test
    void whenDeleteProductsFromShoppingCartAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.deleteProductsFromShoppingCart(WRONG_USERNAME, getTestTwoProductsToDelete()));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenDeleteProductsFromShoppingCartAndShoppingCartNotExist_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete()));

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        assertThat(exception.getUserMessage(), equalTo("Shopping cart contains no product(s): "
                + "0112f4d1-4940-4cd5-84ed-e7d44f683808, 0a53f38d-dd00-4f80-9b2e-c9d17ee46385"));
    }

    @Test
    void whenDeleteProductsFromShoppingCartAndAnyProductNotInShoppingCart_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestSingleProductShoppingCart()));

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete()));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(),
                equalTo("Shopping cart contains no product(s): " + PRODUCT_ID_B + ", " + PRODUCT_ID_C));
    }

    @Test
    void whenDeleteProductsFromShoppingCartAndShoppingCartDeactivated_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestDeactivatedShoppingCart()));

        final ShoppingCartDeactivatedException exception = assertThrows(ShoppingCartDeactivatedException.class,
                () -> service.deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete()));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("User not authorized to modify shopping cart"));
    }

    @Test
    void whenDeleteProductsFromShoppingCart_ThenDeleteProductsAndReturnShoppingCartAndLog() throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));
        when(mockRepository.save(any())).thenReturn(getTestSingleProductShoppingCart());

        final ShoppingCart shoppingCart =
                service.deleteProductsFromShoppingCart(USERNAME_A, getTestTwoProductsToDelete());

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestSingleProductShoppingCart())));
        assertThat(shoppingCart, samePropertyValuesAs(getTestSingleProductShoppingCart()));
        assertLogs(logListener.getEvents(), "delete_products.json", getClass());
    }

    @Test
    void whenChangeProductQuantityAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.changeProductQuantity(WRONG_USERNAME, getTestChangeProductQuantityRequest()));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenChangeProductQuantityAndShoppingCartNotExist_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest()));

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        assertThat(exception.getUserMessage(), equalTo("Shopping cart does not contain product " + PRODUCT_ID_C));
    }

    @Test
    void whenChangeProductQuantityAndProductNotInShoppingCart_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestSingleProductShoppingCart()));

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest()));

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("Shopping cart does not contain product " + PRODUCT_ID_C));
    }

    @Test
    void whenChangeProductQuantityAndShoppingCartDeactivated_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestDeactivatedShoppingCart()));

        final ShoppingCartDeactivatedException exception = assertThrows(ShoppingCartDeactivatedException.class,
                () -> service.changeProductQuantity(USERNAME_A, getTestChangeProductQuantityRequest()));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("User not authorized to modify shopping cart"));
    }

    @Test
    void whenChangeProductQuantity_ThenChangeProductQuantityAndReturnShoppingCartAndLog() throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));
        when(mockRepository.save(any())).thenReturn(getTestFullShoppingCartWithNewQuantity());

        final ShoppingCart shoppingCart = service.changeProductQuantity(USERNAME_A,
                getTestChangeProductQuantityRequest());

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestFullShoppingCartWithNewQuantity())));
        assertThat(shoppingCart, samePropertyValuesAs(getTestFullShoppingCartWithNewQuantity()));
        assertLogs(logListener.getEvents(), "change_product_quantity.json", getClass());
    }

    @Test
    void whenBookProductsInWarehouseAndWrongUsername_ThenThrowException() {

        final NotAuthorizedUserException exception = assertThrows(NotAuthorizedUserException.class,
                () -> service.bookProductsInWarehouse(WRONG_USERNAME));

        assertThat(exception.getUserMessage(), equalTo("User not unauthenticated"));
    }

    @Test
    void whenBookProductsInWarehouseAndShoppingCartNotExist_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockUUIDGenerator.getNewUUID()).thenReturn(SHOPPING_CART_ID);
        when(mockRepository.save(any())).thenReturn(getTestEmptyShoppingCart());

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.bookProductsInWarehouse(USERNAME_A));

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockUUIDGenerator).getNewUUID();
        inOrder.verify(mockRepository).save(argThat(samePropertyValuesAs(getTestEmptyShoppingCart())));
        assertThat(exception.getUserMessage(), equalTo("Shopping cart is empty"));
    }

    @Test
    void whenBookProductsInWarehouseAndShoppingCartEmpty_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestEmptyShoppingCart()));

        final NoProductsInShoppingCartException exception = assertThrows(NoProductsInShoppingCartException.class,
                () -> service.bookProductsInWarehouse(USERNAME_A));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("Shopping cart is empty"));
    }

    @Test
    void whenBookProductsInWarehouseAndShoppingCartDeactivated_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestDeactivatedShoppingCart()));

        final ShoppingCartDeactivatedException exception = assertThrows(ShoppingCartDeactivatedException.class,
                () -> service.bookProductsInWarehouse(USERNAME_A));

        verify(mockRepository).findByUsername(USERNAME_A);
        assertThat(exception.getUserMessage(), equalTo("User not authorized to modify shopping cart"));
    }

    @Test
    void whenBookProductsInWarehouseAndNotEnoughProductsInWarehouse_ThenThrowException() {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());
        when(mockWarehouseService.bookProducts(any()))
                .thenThrow(new ProductInShoppingCartNotInWarehouse(TEST_EXCEPTION_MESSAGE));

        final BookingNotPossibleException exception = assertThrows(BookingNotPossibleException.class,
                () -> service.bookProductsInWarehouse(USERNAME_A));

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockMapper).mapToDto(argThat(samePropertyValuesAs(getTestFullShoppingCart())));
        inOrder.verify(mockWarehouseService).bookProducts(getTestFullShoppingCartDto());
        assertThat(exception.getUserMessage(), equalTo("Cannot book products in warehouse now"));
    }

    @Test
    void whenBookProductsInWarehouse_ThenMapToDtoAndPassToWarehouseServiceAndReturnResponseAndLog() throws Exception {
        when(mockRepository.findByUsername(any())).thenReturn(Optional.of(getTestFullShoppingCart()));
        when(mockMapper.mapToDto(any())).thenReturn(getTestFullShoppingCartDto());
        when(mockWarehouseService.bookProducts(any())).thenReturn(getTestBookedProductsDto());

        final BookedProductsDto dto = service.bookProductsInWarehouse(USERNAME_A);

        inOrder.verify(mockRepository).findByUsername(USERNAME_A);
        inOrder.verify(mockMapper).mapToDto(argThat(samePropertyValuesAs(getTestFullShoppingCart())));
        inOrder.verify(mockWarehouseService).bookProducts(getTestFullShoppingCartDto());
        assertThat(dto, equalTo(getTestBookedProductsDto()));
        assertLogs(logListener.getEvents(), "book_products.json", getClass());
    }
}