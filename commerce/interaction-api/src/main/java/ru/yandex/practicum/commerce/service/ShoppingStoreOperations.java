package ru.yandex.practicum.commerce.service;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.store.Pageable;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreOperations {

    @PostMapping("/api/v1/shopping-store")
    ProductDto addProduct(@RequestBody @Valid ProductDto product);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> findProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid ProductDto product);

    @PostMapping("/api/v1/shopping-store/quantityState")
    boolean setProductQuantity(@Valid SetProductQuantityStateRequest request);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    boolean deleteProduct(@RequestBody UUID productId);
}
