package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.warehouse.model.Product;

public interface ProductService {

    void addNewProduct(Product product);

    BookedProductsDto checkProductsAvailability(ShoppingCartDto shoppingCart);

    void increaseProductQuantity(AddProductToWarehouseRequest request);
}
