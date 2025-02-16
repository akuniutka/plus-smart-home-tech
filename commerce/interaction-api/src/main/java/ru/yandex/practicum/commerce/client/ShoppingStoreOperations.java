package ru.yandex.practicum.commerce.client;

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

    @PostMapping
    ProductDto addProduct(@RequestBody @Valid ProductDto newProductDto);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @GetMapping
    List<ProductDto> findProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto updatedProductDto);

    @PostMapping("/quantityState")
    boolean setProductQuantity(@Valid SetProductQuantityStateRequest request);

    @PostMapping("/removeProductFromStore")
    boolean deleteProduct(@RequestBody UUID productId);
}
