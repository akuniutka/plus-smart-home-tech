package ru.yandex.practicum.commerce.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartState;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.cart.service.WarehouseService;
import ru.yandex.practicum.commerce.cart.util.UUIDGenerator;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ShoppingCartDeactivatedException;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final WarehouseService warehouseService;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository repository;
    private final UUIDGenerator uuidGenerator;

    @Override
    public ShoppingCart getShoppingCartByUsername(final String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("User not unauthenticated");
        }
        return repository.findByUsername(username).orElseGet(() -> createShoppingCart(username));
    }

    @Override
    public ShoppingCart addProductsToShoppingCart(final String username, final Map<UUID, Long> products) {
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        requireShoppingCartNotDeactivated(shoppingCart);
        shoppingCart.getProducts().putAll(products);
        shoppingCart = repository.save(shoppingCart);
        log.info("Put products to shopping cart: shoppingCartId = {}", shoppingCart.getShoppingCartId());
        log.debug("Shopping cart after products added = {}", shoppingCart);
        return shoppingCart;
    }

    @Override
    public void deactivateShoppingCart(final String username) {
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        shoppingCart.setShoppingCartState(ShoppingCartState.DEACTIVATED);
        shoppingCart = repository.save(shoppingCart);
        log.info("Deactivated shopping cart: shoppingCartId = {}", shoppingCart.getShoppingCartId());
        log.debug("Deactivated shopping cart = {}", shoppingCart);
    }

    @Override
    public ShoppingCart deleteProductsFromShoppingCart(final String username, final Set<UUID> products) {
        final ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        final Set<UUID> productsNotInShoppingCart = products.stream()
                .filter(productId -> !shoppingCart.getProducts().containsKey(productId))
                .collect(Collectors.toSet());
        if (!productsNotInShoppingCart.isEmpty()) {
            final String productsNotInShoppingCartStr = productsNotInShoppingCart.stream()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new NoProductsInShoppingCartException("Shopping cart contains no product(s): "
                    + productsNotInShoppingCartStr);
        }
        requireShoppingCartNotDeactivated(shoppingCart);
        products.forEach(shoppingCart.getProducts()::remove);
        final ShoppingCart savedShoppingCart = repository.save(shoppingCart);
        log.info("Deleted products from shopping cart: shoppingCartId = {}", savedShoppingCart.getShoppingCartId());
        log.debug("Shopping cart after products deleted = {}", savedShoppingCart);
        return savedShoppingCart;
    }

    @Override
    public ShoppingCart changeProductQuantity(final String username, final ChangeProductQuantityRequest request) {
        ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        if (!shoppingCart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Shopping cart does not contain product "
                    + request.getProductId());
        }
        requireShoppingCartNotDeactivated(shoppingCart);
        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());
        shoppingCart = repository.save(shoppingCart);
        log.info("Changed product quantity in shopping cart: shoppingCartId = {}, productId = {}, new quantity = {}",
                shoppingCart.getShoppingCartId(), request.getProductId(), request.getNewQuantity());
        log.debug("Shopping cart after product quantity change = {}", shoppingCart);
        return shoppingCart;
    }

    @Override
    public BookedProductsDto bookProductsInWarehouse(final String username) {
        final ShoppingCart shoppingCart = getShoppingCartByUsername(username);
        if (shoppingCart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("Shopping cart is empty");
        }
        requireShoppingCartNotDeactivated(shoppingCart);
        final ShoppingCartDto shoppingCartDto = shoppingCartMapper.mapToDto(shoppingCart);

        final BookedProductsDto bookedProductsDto = warehouseService.checkProductsAvailability(shoppingCartDto);
        log.info("Booked products in warehouse: shoppingCartId = {}, deliveryVolume = {}, deliveryWeight = {}, "
                        + "fragile = {}", shoppingCart.getShoppingCartId(), bookedProductsDto.getDeliveryVolume(),
                bookedProductsDto.getDeliveryWeight(), bookedProductsDto.getFragile());
        log.debug("Booked shopping cart = {}", shoppingCart);
        return bookedProductsDto;
    }

    private ShoppingCart createShoppingCart(final String username) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartId(uuidGenerator.getNewUUID());
        shoppingCart.setUsername(username);
        shoppingCart.setShoppingCartState(ShoppingCartState.ACTIVE);
        shoppingCart = repository.save(shoppingCart);
        log.info("Created new shopping cart: shoppingCartId = {}, username = {}", shoppingCart.getShoppingCartId(),
                shoppingCart.getUsername());
        log.debug("New shopping cart = {}", shoppingCart);
        return shoppingCart;
    }

    private void requireShoppingCartNotDeactivated(final ShoppingCart shoppingCart) {
        if (ShoppingCartState.DEACTIVATED.equals(shoppingCart.getShoppingCartState())) {
            throw new ShoppingCartDeactivatedException("User not authorized to modify shopping cart");
        }
    }
}
