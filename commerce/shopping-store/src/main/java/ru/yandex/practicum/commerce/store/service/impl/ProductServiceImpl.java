package ru.yandex.practicum.commerce.store.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.ProductCategory;
import ru.yandex.practicum.commerce.dto.ProductState;
import ru.yandex.practicum.commerce.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;
import ru.yandex.practicum.commerce.store.service.ProductService;
import ru.yandex.practicum.commerce.store.util.UUIDGenerator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Product addProduct(final Product product) {
        product.setProductId(uuidGenerator.getNewUUID());
        final Product savedProduct = repository.save(product);
        log.info("Added new product: productId = {}, productName = {}", savedProduct.getProductId(),
                savedProduct.getProductName());
        log.debug("Product added = {}", savedProduct);
        return savedProduct;
    }

    @Override
    public Product getProductById(final UUID productId) {
        return repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product not found: productId = " + productId)
        );
    }

    @Override
    public List<Product> findProductsByCategory(final ProductCategory category, final Pageable pageable) {
        return repository.findAllByProductCategory(category, pageable);
    }

    @Override
    public Product updateProduct(final Product product) {
        if (!repository.existsById(product.getProductId())) {
            throw new ProductNotFoundException("Product not found: productId = " + product.getProductId());
        }
        final Product savedProduct = repository.save(product);
        log.info("Updated product: productId = {}, productName = {}", savedProduct.getProductId(),
                savedProduct.getProductName());
        log.debug("Product updated = {}", savedProduct);
        return savedProduct;
    }

    @Override
    public boolean setProductQuantity(final SetProductQuantityStateRequest request) {
        final Product product = getProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        final Product savedProduct = repository.save(product);
        log.info("Updated product quantity: productId = {}, productName = {}, quantityState = {}",
                savedProduct.getProductId(), savedProduct.getProductName(), savedProduct.getQuantityState());
        log.debug("Product with new quantity = {}", savedProduct);
        return true;
    }

    @Override
    public boolean deleteProductById(final UUID productId) {
        final Product product = getProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        final Product savedProduct = repository.save(product);
        log.info("Deleted product: productId = {}, productName = {}, productState = {}",
                savedProduct.getProductId(), savedProduct.getProductName(), savedProduct.getProductState());
        log.debug("Product with new state = {}", savedProduct);
        return true;
    }
}
