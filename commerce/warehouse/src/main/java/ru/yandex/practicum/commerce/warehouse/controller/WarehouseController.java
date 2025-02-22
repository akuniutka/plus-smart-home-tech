package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.mapper.BookingMapper;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController implements WarehouseOperations {

    private final ProductService productService;
    private final AddressService addressService;
    private final ProductMapper productMapper;
    private final BookingMapper bookingMapper;

    @Override
    public void addNewProduct(final NewProductInWarehouseRequest request) {
        log.info("Received request to add new product: productId = {}", request.getProductId());
        log.debug("Add new product request = {}", request);
        final Product product = productMapper.mapToEntity(request);
        productService.addNewProduct(product);
        log.info("Responded with 200 OK to add new product request: productId = {}", request.getProductId());
    }

    @Override
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        log.info("Received request to increase product quantity: productId = {}, quantity to add = {}",
                request.getProductId(), request.getQuantity());
        productService.increaseProductQuantity(request);
        log.info("Responded with 200 OK to increase product quantity: productId = {}, quantity to add = {}",
                request.getProductId(), request.getQuantity());
    }

    @Override
    public BookedProductsDto checkProductsAvailability(final ShoppingCartDto shoppingCart) {
        log.info("Received request to check products availability for shopping cart: shoppingCartId = {}",
                shoppingCart.getShoppingCartId());
        log.debug("Shopping cart = {}", shoppingCart);
        final DeliveryParams deliveryParams = productService.checkProductsAvailability(shoppingCart);
        final BookedProductsDto dto = bookingMapper.mapToDto(deliveryParams);
        log.info("Responded with delivery parameters for shopping cart: shoppingCartId = {}",
                shoppingCart.getShoppingCartId());
        log.debug("Shopping cart delivery parameters = {}", dto);
        return dto;
    }

    @Override
    public BookedProductsDto bookProducts(final AssemblyProductsForOrderRequest request) {
        log.info("Received request to book products for order: orderId = {}", request.getOrderId());
        log.debug("Booking request = {}", request);
        final DeliveryParams deliveryParams = productService.bookProducts(request);
        final BookedProductsDto dto = bookingMapper.mapToDto(deliveryParams);
        log.info("Responded with delivery parameters for order: orderId = {}", request.getOrderId());
        log.debug("Order delivery parameters = {}", deliveryParams);
        return dto;
    }

    @Override
    public void shippedToDelivery(final ShippedToDeliveryRequest request) {
        log.info("Received request to mark order as picked by delivery service: orderId = {}, deliveryId = {}",
                request.getOrderId(), request.getDeliveryId());
        productService.shippedToDelivery(request);
        log.info("Responded with 200 OK to mark order as picked by delivery service: orderId = {}, deliveryId = {}",
                request.getOrderId(), request.getDeliveryId());
    }

    @Override
    public void returnProducts(final Map<UUID, Long> products) {
        log.info("Received request to return products to warehouse");
        log.debug("Returned products = {}", products);
        productService.returnProducts(products);
        log.info("Responded with 200 OK to return products to warehouse");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Received request for warehouse address");
        final AddressDto address = addressService.getAddress();
        log.info("Responded with warehouse address");
        log.debug("Warehouse address = {}", address);
        return address;
    }
}
