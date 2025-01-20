package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.Pageable;
import ru.yandex.practicum.commerce.dto.ProductCategory;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @PutMapping("/api/v1/shopping-store")
    ProductDto addProduct(@RequestBody @Valid ProductDto product);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> findProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid ProductDto product);

    @PostMapping("/api/v1/shopping-store/quantityState")
    boolean setProductQuantity(@Valid SetProductQuantityStateRequest request);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    boolean deleteProduct(@RequestBody UUID productId);
}
