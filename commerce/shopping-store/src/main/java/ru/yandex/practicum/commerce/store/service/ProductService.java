package ru.yandex.practicum.commerce.store.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.store.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addProduct(Product product);

    Product getProductById(UUID productId);

    List<Product> findProductsByCategory(ProductCategory category, Pageable pageable);

    Product updateProduct(Product product);

    boolean setProductQuantity(SetProductQuantityStateRequest request);

    boolean deleteProductById(UUID productId);
}
