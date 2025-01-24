package ru.yandex.practicum.commerce.cart.util;

import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartState;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TestModels {

    public static final String USERNAME_A = "alice";
    public static final String USERNAME_B = "bob";
    public static final String WRONG_USERNAME = "";
    public static final UUID SHOPPING_CART_ID = UUID.fromString("801b5a89-c5f1-435c-a54e-d06cd6662a6a");
    public static final UUID PRODUCT_ID_A = UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25");
    public static final UUID PRODUCT_ID_B = UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808");
    public static final UUID PRODUCT_ID_C = UUID.fromString("0a53f38d-dd00-4f80-9b2e-c9d17ee46385");
    public static final long PRODUCT_QUANTITY_A = 1L;
    public static final long PRODUCT_QUANTITY_B = 2L;
    public static final long PRODUCT_QUANTITY_C = 4L;
    public static final long NEW_PRODUCT_QUANTITY_C = 16L;

    public static final String TEST_EXCEPTION_MESSAGE = "Test exception message";

    public static final BigDecimal DELIVERY_VOLUME = BigDecimal.valueOf(100, 2);
    public static final BigDecimal DELIVERY_WEIGHT = BigDecimal.valueOf(1000, 2);
    public static final boolean IS_FRAGILE = true;

    private TestModels() {
        throw new AssertionError();
    }

    public static ShoppingCart getTestEmptyShoppingCart() {
        final ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartId(SHOPPING_CART_ID);
        shoppingCart.setUsername(USERNAME_A);
        shoppingCart.setShoppingCartState(ShoppingCartState.ACTIVE);
        return shoppingCart;
    }

    public static ShoppingCart getTestSingleProductShoppingCart() {
        final ShoppingCart shoppingCart = getTestEmptyShoppingCart();
        shoppingCart.getProducts().put(PRODUCT_ID_A, PRODUCT_QUANTITY_A);
        return shoppingCart;
    }

    public static ShoppingCart getTestFullShoppingCart() {
        final ShoppingCart shoppingCart = getTestSingleProductShoppingCart();
        shoppingCart.getProducts().putAll(getTestTwoProductsToAdd());
        return shoppingCart;
    }

    public static ShoppingCart getTestFullShoppingCartWithNewQuantity() {
        final ShoppingCart shoppingCart = getTestFullShoppingCart();
        shoppingCart.getProducts().put(PRODUCT_ID_C, NEW_PRODUCT_QUANTITY_C);
        return shoppingCart;
    }

    public static ShoppingCart getTestEmptyDeactivatedShoppingCart() {
        final ShoppingCart shoppingCart = getTestEmptyShoppingCart();
        shoppingCart.setShoppingCartState(ShoppingCartState.DEACTIVATED);
        return shoppingCart;
    }

    public static ShoppingCart getTestDeactivatedShoppingCart() {
        final ShoppingCart shoppingCart = getTestFullShoppingCart();
        shoppingCart.setShoppingCartState(ShoppingCartState.DEACTIVATED);
        return shoppingCart;
    }

    public static Map<UUID, Long> getTestThreeProductsToAdd() {
        return Map.of(
                PRODUCT_ID_A, PRODUCT_QUANTITY_A,
                PRODUCT_ID_B, PRODUCT_QUANTITY_B,
                PRODUCT_ID_C, PRODUCT_QUANTITY_C
        );
    }

    public static Map<UUID, Long> getTestTwoProductsToAdd() {
        return Map.of(
                PRODUCT_ID_B, PRODUCT_QUANTITY_B,
                PRODUCT_ID_C, PRODUCT_QUANTITY_C
        );
    }

    public static Set<UUID> getTestTwoProductsToDelete() {
        return Set.of(PRODUCT_ID_B, PRODUCT_ID_C);
    }

    public static ChangeProductQuantityRequest getTestChangeProductQuantityRequest() {
        final ChangeProductQuantityRequest request = new ChangeProductQuantityRequest();
        request.setProductId(PRODUCT_ID_C);
        request.setNewQuantity(NEW_PRODUCT_QUANTITY_C);
        return request;
    }

    public static ShoppingCartDto getTestFullShoppingCartDto() {
        final ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(SHOPPING_CART_ID);
        dto.setProducts(getTestThreeProductsToAdd());
        return dto;
    }

    public static BookedProductsDto getTestBookedProductsDto() {
        final BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryVolume(DELIVERY_VOLUME);
        dto.setDeliveryWeight(DELIVERY_WEIGHT);
        dto.setFragile(IS_FRAGILE);
        return dto;
    }
}
