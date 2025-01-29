package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.service.ShoppingCartOperations;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCartByUsername(final String username) {
        log.info("Received request for shopping cart by username: username = {}", "***");
        final ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByUsername(username);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with requested shopping cart: shoppingCartId = {}, username = {}", dto.getShoppingCartId(),
                "***");
        log.debug("Requested shopping cart = {}", dto);
        return dto;
    }

    @Override
    public ShoppingCartDto addProductsToShoppingCart(final String username, final Map<UUID, Long> products) {
        log.info("Received request to put {} product(s) to shopping cart: username = {}", products.size(), "***");
        final ShoppingCart shoppingCart = shoppingCartService.addProductsToShoppingCart(username, products);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with updated shopping cart: shoppingCartId = {}, username = {}, products put = {}",
                dto.getShoppingCartId(), "***", products.size());
        log.debug("Shopping cart after product(s) put = {}", dto);
        return dto;
    }

    @Override
    public void deactivateShoppingCartByUsername(final String username) {
        log.info("Received request to deactivate shopping cart: username = {}", "***");
        shoppingCartService.deactivateShoppingCart(username);
        log.info("Responded with 200 OK to deactivate shopping cart request: username = {}", "***");
    }

    @Override
    public ShoppingCartDto deleteProductsFromShoppingCart(final String username, final Set<UUID> products) {
        log.info("Received request to delete {} product(s) from shopping cart: username = {}", products.size(), "***");
        final ShoppingCart shoppingCart = shoppingCartService.deleteProductsFromShoppingCart(username, products);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with updated shopping cart: shoppingCartId = {}, username = {}, products deleted = {}",
                dto.getShoppingCartId(), "***", products.size());
        log.debug("Shopping cart after product(s) deleted = {}", dto);
        return dto;
    }

    @Override
    public ShoppingCartDto changeProductQuantity(final String username, final ChangeProductQuantityRequest request) {
        log.info("Received request to change product quantity in shopping cart: username = {}, productId = {}", "***",
                request.getProductId());
        log.debug("Change product quantity request = {}", request);
        final ShoppingCart shoppingCart = shoppingCartService.changeProductQuantity(username, request);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with updated shopping cart: shoppingCartId = {}, username = {}, productId = {}",
                dto.getShoppingCartId(), "***", request.getProductId());
        log.debug("Shopping cart after product quantity changed = {}", dto);
        return dto;
    }

    @Override
    public BookedProductsDto bookProductsInWarehouse(final String username) {
        log.info("Received request to book products in warehouse: username = {}", "***");
        final BookedProductsDto dto = shoppingCartService.bookProductsInWarehouse(username);
        log.info("Responded with booking parameters: username = {}, deliveryVolume = {}, deliveryWeight = {}, fragile "
                + "= {}", "***", dto.getDeliveryVolume(), dto.getDeliveryWeight(), dto.getFragile());
        return dto;
    }
}
