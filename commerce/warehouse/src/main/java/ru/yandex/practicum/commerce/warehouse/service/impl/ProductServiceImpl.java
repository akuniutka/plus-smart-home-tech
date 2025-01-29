package ru.yandex.practicum.commerce.warehouse.service.impl;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final int DIMENSION_SCALE = 3;
    private final ProductRepository productRepository;

    @Override
    public void addNewProduct(final Product product) {
        if (productRepository.existsById(product.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product with productId " + product.getProductId()
                    + " already exists");
        }
        final Product savedProduct = productRepository.save(product);
        log.info("Added new product to warehouse: productId = {}", savedProduct.getProductId());
        log.debug("Added product = {}", product);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public BookedProductsDto bookProductsInWarehouse(final ShoppingCartDto shoppingCart) {
        final Map<UUID, Product> products = getProductsByIds(shoppingCart.getProducts().keySet());
        requireAllProductsExist(shoppingCart, products);
        requireAllProductsSuffice(shoppingCart, products);

        final BookedProductsDto bookedProducts = initBookedProductsDto();
        shoppingCart.getProducts().forEach((productId, quantity) ->
            bookProductInWarehouse(products.get(productId), quantity, bookedProducts)
        );
        productRepository.saveAll(products.values());
        log.info("Booked products for shopping cart: shoppingCartId = {}", shoppingCart.getShoppingCartId());
        log.debug("Shopping cart = {}", shoppingCart);
        log.debug("Updated products in warehouse = {}", products.values());
        return bookedProducts;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Product with productId " + request.getProductId()
                        + " does not exist")
        );
        product.setTotalQuantity(product.getTotalQuantity() + request.getQuantity());
        product = productRepository.save(product);
        log.info("Increased quantity of product in warehouse: productId = {}, new quantity = {}",
                product.getProductId(), product.getTotalQuantity());
        log.debug("Updated product in warehouse = {}", product);
    }

    private Map<UUID, Product> getProductsByIds(final Set<UUID> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
    }

    private void requireAllProductsExist(final ShoppingCartDto shoppingCart, Map<UUID, Product> products) {
        final Set<UUID> notFound = shoppingCart.getProducts().keySet().stream()
                .filter(productId -> !products.containsKey(productId))
                .collect(Collectors.toSet());
        if (!notFound.isEmpty()) {
            throw new ProductInShoppingCartNotInWarehouse("Products not found in warehouse: " + toString(notFound));
        }
    }

    private void requireAllProductsSuffice(final ShoppingCartDto shoppingCart, Map<UUID, Product> products) {
        final Set<UUID> insufficient = new HashSet<>();
        shoppingCart.getProducts().forEach((productId, quantity) -> {
            Product product = products.get(productId);
            if (quantity > product.getTotalQuantity() - product.getBookedQuantity()) {
                insufficient.add(productId);
            }
        });
        if (!insufficient.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Insufficient stocks in warehouse: "
                    + toString(insufficient));
        }
    }

    private String toString(final Set<UUID> productIds) {
        return productIds.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private BookedProductsDto initBookedProductsDto() {
        final BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryVolume(BigDecimal.valueOf(0L, DIMENSION_SCALE));
        dto.setDeliveryWeight(BigDecimal.valueOf(0L, DIMENSION_SCALE));
        dto.setFragile(false);
        return dto;
    }

    private void bookProductInWarehouse(final Product product, final long quantity, final BookedProductsDto booked) {
        final BigDecimal _quantity = BigDecimal.valueOf(quantity);
        final Dimension dimension = product.getDimension();
        booked.setDeliveryVolume(dimension.getWidth()
                .multiply(dimension.getHeight())
                .multiply(dimension.getDepth())
                .multiply(_quantity)
                .setScale(DIMENSION_SCALE, RoundingMode.HALF_UP)
                .add(booked.getDeliveryVolume()));
        booked.setDeliveryWeight(product.getWeight()
                .multiply(_quantity)
                .add(booked.getDeliveryWeight()));
        booked.setFragile(booked.getFragile() || Boolean.TRUE.equals(product.getFragile()));
        product.setBookedQuantity(product.getBookedQuantity() + quantity);
    }
}
