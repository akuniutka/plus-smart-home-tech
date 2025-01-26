package ru.yandex.practicum.commerce.warehouse.service;

import ru.yandex.practicum.commerce.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.warehouse.model.Product;

public interface ProductService {

    void addNewProduct(Product product);

    BookedProductsDto bookProductsInWarehouse(ShoppingCartDto shoppingCart);

    void increaseProductQuantity(AddProductToWarehouseRequest request);
}
