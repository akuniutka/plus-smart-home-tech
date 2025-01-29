package ru.yandex.practicum.commerce.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartMapper shoppingCartMapper;

    @GetMapping
    public ShoppingCartDto getShoppingCartByUsername(@RequestParam final String username) {
        log.info("Received request for shopping cart by username: username = {}", username);
        final ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByUsername(username);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with requested shopping cart: shoppingCartId = {}, username = {}", dto.getShoppingCartId(),
                username);
        log.debug("Requested shopping cart = {}", dto);
        return dto;
    }

    @PutMapping
    public ShoppingCartDto addProductsToShoppingCart(@RequestParam final String username,
            @RequestBody final Map<UUID, Long> products) {
        log.info("Received request to put products to shopping cart: username = {}", username);
        final ShoppingCart shoppingCart = shoppingCartService.addProductsToShoppingCart(username, products);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with shopping cart after products put: shoppingCartId = {}, username = {}",
                dto.getShoppingCartId(), username);
        log.debug("Shopping cart after products put = {}", dto);
        return dto;
    }

    @DeleteMapping
    public void deactivateShoppingCartByUsername(@RequestParam final String username) {
        log.info("Received request to deactivate shopping cart: username = {}", username);
        shoppingCartService.deactivateShoppingCart(username);
        log.info("Responded with 200 OK to deactivate shopping cart request: username = {}", username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto deleteProductsFromShoppingCart(@RequestParam final String username,
            @RequestBody final Set<UUID> products) {
        log.info("Received request to delete products from shopping cart: username = {}", username);
        final ShoppingCart shoppingCart = shoppingCartService.deleteProductsFromShoppingCart(username, products);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with shopping cart after products deleted: shoppingCartId = {}, username = {}",
                dto.getShoppingCartId(), username);
        log.debug("Shopping cart after products deleted = {}", dto);
        return dto;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam final String username,
            @RequestBody @Valid final ChangeProductQuantityRequest request) {
        log.info("Received request to change product quantity in shopping cart: username = {}, productId = {}",
                username, request.getProductId());
        log.debug("Change product quantity request = {}", request);
        final ShoppingCart shoppingCart = shoppingCartService.changeProductQuantity(username, request);
        final ShoppingCartDto dto = shoppingCartMapper.mapToDto(shoppingCart);
        log.info("Responded with updated shopping cart: shoppingCartId = {}, username = {}, productId = {}",
                dto.getShoppingCartId(), username, request.getProductId());
        log.debug("Shopping cart after product quantity changed = {}", dto);
        return dto;
    }

    @PostMapping("/booking")
    public BookedProductsDto bookProductsInWarehouse(@RequestParam final String username) {
        log.info("Received request to book products in warehouse: username = {}", username);
        final BookedProductsDto dto = shoppingCartService.bookProductsInWarehouse(username);
        log.info("Responded with booking parameters: username = {}, deliveryVolume = {}, deliveryWeight = {}, fragile "
                + "= {}", username, dto.getDeliveryVolume(), dto.getDeliveryWeight(), dto.getFragile());
        return dto;
    }
}
