package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;
import ru.yandex.practicum.commerce.warehouse.model.Product;

public interface ProductService {

    void addNewProduct(Product product);

    void increaseProductQuantity(AddProductToWarehouseRequest request);

    DeliveryParams checkProductsAvailability(ShoppingCartDto shoppingCart);

    DeliveryParams bookProducts(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);
}
