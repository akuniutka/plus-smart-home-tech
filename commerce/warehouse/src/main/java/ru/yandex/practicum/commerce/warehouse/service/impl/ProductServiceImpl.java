package ru.yandex.practicum.commerce.warehouse.service.impl;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.BookedProducts;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.model.Stock;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.repository.StockRepository;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;

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

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Override
    public void addNewProduct(final Product product) {
        if (productRepository.existsById(product.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Product with productId " + product.getProductId()
                    + " already exists");
        }
        final Product savedProduct = productRepository.save(product);
        final Stock initialStock = createInitialStock(savedProduct.getProductId());
        final Stock savedStock = stockRepository.save(initialStock);
        log.info("Added new product to warehouse: productId = {}", savedProduct.getProductId());
        log.debug("Added product = {}", product);
        log.debug("Initial stock = {}", savedStock);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public BookedProducts bookProductsInWarehouse(final ShoppingCartDto shoppingCart) {
        final Set<UUID> productIds = shoppingCart.getProducts().keySet();
        final Map<UUID, Product> products = getProductsByIds(productIds);
        final Map<UUID, Stock> stocks = getStocksByIds(productIds);

        final BookedProducts bookedProducts = new BookedProducts();
        final Set<UUID> notFound = new HashSet<>();
        final Set<UUID> insufficient = new HashSet<>();
        shoppingCart.getProducts().forEach((productId, quantity) -> {
            Stock stock = stocks.get(productId);
            if (stock == null) {
                notFound.add(productId);
            } else if (quantity > stock.getTotalQuantity() - stock.getBookedQuantity()) {
                insufficient.add(productId);
            } else {
                bookedProducts.addProduct(products.get(productId), quantity);
                stock.setBookedQuantity(stock.getBookedQuantity() + quantity);
            }
        });

        if (!notFound.isEmpty()) {
            throw new ProductInShoppingCartNotInWarehouse("Products not found: " + toString(notFound));
        } else if (!insufficient.isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Insufficient stocks: " + toString(insufficient));
        }
        stockRepository.saveAll(stocks.values());
        log.info("Booked products for shopping cart: shoppingCartId = {}", shoppingCart.getShoppingCartId());
        log.debug("Shopping cart = {}", shoppingCart);
        log.debug("Stocks after booking = {}", stocks.values());
        return bookedProducts;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        Stock stock = stockRepository.findById(request.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Product with productId " + request.getProductId()
                        + "does not exist")
        );
        stock.setTotalQuantity(stock.getTotalQuantity() + request.getQuantity());
        stock = stockRepository.save(stock);
        log.info("Increased quantity of product in warehouse: productId = {}, new quantity = {}",
                stock.getProductId(), stock.getTotalQuantity());
        log.debug("Updated stock = {}", stock);
    }

    private Stock createInitialStock(final UUID productId) {
        final Stock stock = new Stock();
        stock.setProductId(productId);
        return stock;
    }

    private Map<UUID, Product> getProductsByIds(final Set<UUID> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
    }

    private Map<UUID, Stock> getStocksByIds(final Set<UUID> productIds) {
        return stockRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Stock::getProductId, Function.identity()));
    }

    private String toString(final Set<UUID> productIds) {
        return productIds.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
