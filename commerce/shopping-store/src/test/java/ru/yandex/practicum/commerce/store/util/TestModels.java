package ru.yandex.practicum.commerce.store.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.commerce.dto.store.Pageable;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.ProductState;
import ru.yandex.practicum.commerce.dto.store.QuantityState;
import ru.yandex.practicum.commerce.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class TestModels {

    public static final UUID PRODUCT_ID_A = UUID.fromString("25182563-067b-441c-b11d-9ad1fb249e25");
    public static final String PRODUCT_NAME_A = "light sensor v.1";
    public static final String DESCRIPTION_A = "A modern energy efficient light sensor";
    public static final String IMAGE_SRC_A = "/sensors/light/light-sensor-v1.jpg";
    public static final QuantityState QUANTITY_A = QuantityState.ENOUGH;
    public static final ProductState PRODUCT_STATE_A = ProductState.ACTIVE;
    public static final ProductCategory CATEGORY_A = ProductCategory.SENSORS;
    public static final BigDecimal PRICE_A = BigDecimal.valueOf(999, 2);

    public static final UUID PRODUCT_ID_B = UUID.fromString("0112f4d1-4940-4cd5-84ed-e7d44f683808");
    public static final String PRODUCT_NAME_B = "lighting device v.2";
    public static final String DESCRIPTION_B = "A new version of smart lighting device";
    public static final String IMAGE_SRC_B = "/lighting/lighting-device-v2.jpg";
    public static final QuantityState QUANTITY_B = QuantityState.FEW;
    public static final ProductState PRODUCT_STATE_B = ProductState.DEACTIVATE;
    public static final ProductCategory CATEGORY_B = ProductCategory.LIGHTING;
    public static final BigDecimal PRICE_B = BigDecimal.valueOf(499, 2);

    public static final String[] SORT_BY = {"price", "productName"};
    public static final String EXCEPTION_MESSAGE = "Test exception";
    public static final org.springframework.data.domain.Pageable PAGEABLE = PageRequest.of(1, 2, Sort.by(SORT_BY));

    private TestModels() {
        throw new AssertionError();
    }

    public static List<ProductDto> getTestProductDtoList() {
        return List.of(getTestProductDtoA(), getTestProductDtoB());
    }

    public static ProductDto getTestNewProductDto() {
        return getTestProductDtoA(null);
    }

    public static ProductDto getTestProductDtoA() {
        return getTestProductDtoA(PRODUCT_ID_A);
    }

    public static ProductDto getTestProductDtoA(final UUID productId) {
        final ProductDto dto = new ProductDto();
        dto.setProductId(productId);
        dto.setProductName(PRODUCT_NAME_A);
        dto.setDescription(DESCRIPTION_A);
        dto.setImageSrc(IMAGE_SRC_A);
        dto.setQuantityState(QUANTITY_A);
        dto.setProductState(PRODUCT_STATE_A);
        dto.setProductCategory(CATEGORY_A);
        dto.setPrice(PRICE_A);
        return dto;
    }

    public static ProductDto getTestProductDtoB() {
        final ProductDto dto = new ProductDto();
        dto.setProductId(PRODUCT_ID_B);
        dto.setProductName(PRODUCT_NAME_B);
        dto.setDescription(DESCRIPTION_B);
        dto.setImageSrc(IMAGE_SRC_B);
        dto.setQuantityState(QUANTITY_B);
        dto.setProductState(PRODUCT_STATE_B);
        dto.setProductCategory(CATEGORY_B);
        dto.setPrice(PRICE_B);
        return dto;
    }

    public static Pageable getTestPageable() {
        final Pageable pageable = new Pageable();
        pageable.setPage(PAGEABLE.getPageNumber());
        pageable.setSize(PAGEABLE.getPageSize());
        pageable.setSort(SORT_BY);
        return pageable;
    }

    public static List<Product> getTestProductList() {
        return List.of(getTestProductA(), getTestProductB());
    }

    public static Product getTestNewProduct() {
        return getTestProductA(null, QUANTITY_A, PRODUCT_STATE_A);
    }

    public static Product getTestProductA() {
        return getTestProductA(PRODUCT_ID_A, QUANTITY_A, PRODUCT_STATE_A);
    }

    public static Product getTestProductAWithUpdatedQuantity() {
        return getTestProductA(PRODUCT_ID_A, QUANTITY_B, PRODUCT_STATE_A);
    }

    public static Product getTestProductADeactivated() {
        return getTestProductA(PRODUCT_ID_A, QUANTITY_A, ProductState.DEACTIVATE);
    }

    public static Product getTestProductA(UUID productId, QuantityState quantity, ProductState state) {
        final Product product = new Product();
        product.setProductId(productId);
        product.setProductName(PRODUCT_NAME_A);
        product.setDescription(DESCRIPTION_A);
        product.setImageSrc(IMAGE_SRC_A);
        product.setQuantityState(quantity);
        product.setProductState(state);
        product.setProductCategory(CATEGORY_A);
        product.setPrice(PRICE_A);
        return product;
    }

    public static Product getTestProductB() {
        final Product product = new Product();
        product.setProductId(PRODUCT_ID_B);
        product.setProductName(PRODUCT_NAME_B);
        product.setDescription(DESCRIPTION_B);
        product.setImageSrc(IMAGE_SRC_B);
        product.setQuantityState(QUANTITY_B);
        product.setProductState(PRODUCT_STATE_B);
        product.setProductCategory(CATEGORY_B);
        product.setPrice(PRICE_B);
        return product;
    }

    public static SetProductQuantityStateRequest getTestSetProductQuantityRequest() {
        final SetProductQuantityStateRequest request = new SetProductQuantityStateRequest();
        request.setProductId(PRODUCT_ID_A);
        request.setQuantityState(QUANTITY_B);
        return request;
    }

    public static ProductNotFoundException getTestProductNotFoundException() {
        return new ProductNotFoundException(EXCEPTION_MESSAGE);
    }
}
