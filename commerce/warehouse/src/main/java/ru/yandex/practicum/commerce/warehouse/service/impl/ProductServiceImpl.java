package ru.yandex.practicum.commerce.warehouse.service.impl;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.commerce.warehouse.service.ProductService;
import ru.yandex.practicum.commerce.warehouse.util.UUIDGenerator;

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
    private final OrderBookingRepository orderBookingRepository;
    private final UUIDGenerator uuidGenerator;

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
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Product with productId " + request.getProductId()
                        + " does not exist")
        );
        product.setQuantity(product.getQuantity() + request.getQuantity());
        product = productRepository.save(product);
        log.info("Increased quantity of product in warehouse: productId = {}, new quantity = {}",
                product.getProductId(), product.getQuantity());
        log.debug("Updated product in warehouse = {}", product);
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    public DeliveryParams checkProductsAvailability(final ShoppingCartDto shoppingCart) {
        return bookProductsInternally(shoppingCart.getProducts(), BookingMode.DRY_RUN);
    }

    @Override
    public DeliveryParams bookProducts(final AssemblyProductsForOrderRequest request) {
        final DeliveryParams deliveryParams = bookProductsInternally(request.getProducts(), BookingMode.BOOK);
        OrderBooking orderBooking = new OrderBooking();
        orderBooking.setOrderBookingId(uuidGenerator.getNewUUID());
        orderBooking.setProducts(request.getProducts());
        orderBooking.setOrderId(request.getOrderId());
        orderBooking = orderBookingRepository.save(orderBooking);
        log.info("Booked products for order: orderId = {}, orderBookingId = {}", orderBooking.getOrderId(),
                orderBooking.getOrderBookingId());
        log.debug("OrderBooking = {}", orderBooking);
        return deliveryParams;
    }

    private DeliveryParams bookProductsInternally(final Map<UUID, Long> products, final BookingMode bookingMode) {
        final Map<UUID, Product> stocks = getStocks(products.keySet());
        requireAllProductsExist(products, stocks);
        requireAllProductsSuffice(products, stocks);
        if (bookingMode == BookingMode.BOOK) {
            products.forEach((productId, quantity) -> decreaseStock(stocks.get(productId), quantity));
            productRepository.saveAll(stocks.values());
        }
        return products.entrySet().stream()
                .map(entry -> DeliveryParams.fromProduct(stocks.get(entry.getKey())).multiply(entry.getValue()))
                .reduce(DeliveryParams.empty(), DeliveryParams::sum);
    }

    private Map<UUID, Product> getStocks(final Set<UUID> productIds) {
        return productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
    }

    private void requireAllProductsExist(final Map<UUID, Long> products, Map<UUID, Product> stocks) {
        final Set<UUID> notFound = products.keySet().stream()
                .filter(productId -> !stocks.containsKey(productId))
                .collect(Collectors.toSet());
        if (!notFound.isEmpty()) {
            throw new ProductInShoppingCartNotInWarehouse("Products not found in warehouse: " + toString(notFound));
        }
    }

    private void requireAllProductsSuffice(final Map<UUID, Long> products, Map<UUID, Product> stocks) {
        final Set<UUID> insufficient = products.entrySet().stream()
                .filter(entry -> entry.getValue() > stocks.get(entry.getKey()).getQuantity())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
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

    private void decreaseStock(final Product product, final long subtrahend) {
        product.setQuantity(product.getQuantity() - subtrahend);
    }

    private enum BookingMode {
        DRY_RUN,
        BOOK
    }
}
