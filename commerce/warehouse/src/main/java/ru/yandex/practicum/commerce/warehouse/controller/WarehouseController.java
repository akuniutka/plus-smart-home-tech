package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController {

    private final ProductService productService;
    private final AddressService addressService;
    private final ProductMapper productMapper;

    @PutMapping
    public void addNewProduct(@RequestBody @Valid final NewProductInWarehouseRequest request) {
        log.info("Received request to add new product: productId = {}", request.getProductId());
        log.debug("Add new product request = {}", request);
        final Product product = productMapper.mapToEntity(request);
        productService.addNewProduct(product);
        log.info("Responded with 200 OK to add new product request: productId = {}", request.getProductId());
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsAvailability(@RequestBody @Valid final ShoppingCartDto shoppingCart) {
        log.info("Received request to check products availability for shopping cart: shoppingCartId = {}",
                shoppingCart.getShoppingCartId());
        log.debug("Shopping cart = {}", shoppingCart);
        final BookedProductsDto dto = productService.checkProductsAvailability(shoppingCart);
        log.info("Responded with delivery parameters for shopping cart: shoppingCartId = {}",
                shoppingCart.getShoppingCartId());
        log.debug("Delivery parameters = {}", dto);
        return dto;
    }

    @PostMapping("/add")
    public void increaseProductQuantity(@RequestBody @Valid final AddProductToWarehouseRequest request) {
        log.info("Received request to increase product quantity: productId = {}, quantity to add = {}",
                request.getProductId(), request.getQuantity());
        productService.increaseProductQuantity(request);
        log.info("Responded with 200 OK to increase product quantity: productId = {}, quantity to add = {}",
                request.getProductId(), request.getQuantity());
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Received request for warehouse address");
        final AddressDto address = addressService.getAddress();
        log.info("Responded with warehouse address");
        log.debug("Warehouse address = {}", address);
        return address;
    }
}
