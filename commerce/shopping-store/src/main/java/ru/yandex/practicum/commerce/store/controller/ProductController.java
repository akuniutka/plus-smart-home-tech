package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.service.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.store.Pageable;
import ru.yandex.practicum.commerce.dto.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.store.ProductDto;
import ru.yandex.practicum.commerce.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController implements ShoppingStoreOperations {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    public ProductDto addProduct(final ProductDto newProductDto) {
        log.info("Received request to add new product: productName = {}", newProductDto.getProductName());
        log.debug("New product = {}", newProductDto);
        final Product newProduct = productMapper.mapToEntity(newProductDto);
        final Product savedProduct = productService.addProduct(newProduct);
        final ProductDto savedProductDto = productMapper.mapToDto(savedProduct);
        log.info("Responded with added product: productId = {}, productName = {}", savedProductDto.getProductId(),
                savedProductDto.getProductName());
        log.debug("Added product = {}", savedProductDto);
        return savedProductDto;
    }

    @Override
    public ProductDto getProductById(final UUID productId) {
        log.info("Received request for product: productId = {}", productId);
        final Product product = productService.getProductById(productId);
        final ProductDto dto = productMapper.mapToDto(product);
        log.info("Responded with requested product: productId = {}, productName = {}", dto.getProductId(),
                dto.getProductName());
        log.debug("Requested product = {}", dto);
        return dto;
    }

    @Override
    public List<ProductDto> findProductsByCategory(final ProductCategory category, final Pageable pageable) {
        log.info("Received request for products in category: category = {}", category);
        log.debug("Requested page = {}, page size = {}, sort by {}", pageable.getPage(), pageable.getSize(),
                pageable.getSort());
        final PageRequest page = PageRequest.of(pageable.getPage(), pageable.getSize(), Sort.by(pageable.getSort()));
        final List<Product> products = productService.findProductsByCategory(category, page);
        final List<ProductDto> dtos = productMapper.mapToDto(products);
        log.info("Responded with {} products in category: category = {}", dtos.size(), category);
        log.debug("Requested products = {}", dtos);
        return dtos;
    }

    @Override
    public ProductDto updateProduct(final ProductDto updatedProductDto) {
        log.info("Received request to update product: productId = {}, productName = {}",
                updatedProductDto.getProductId(), updatedProductDto.getProductName());
        log.debug("Update for product = {}", updatedProductDto);
        final Product updatedProduct = productMapper.mapToEntity(updatedProductDto);
        final Product savedProduct = productService.updateProduct(updatedProduct);
        final ProductDto savedProductDto = productMapper.mapToDto(savedProduct);
        log.info("Responded with updated product: productId = {}, productName = {}", savedProductDto.getProductId(),
                savedProductDto.getProductName());
        return savedProductDto;
    }

    @Override
    public boolean setProductQuantity(final SetProductQuantityStateRequest request) {
        log.info("Received request to update product quantity: productId = {}, quantityState = {}",
                request.getProductId(), request.getQuantityState());
        final boolean result = productService.setProductQuantity(request);
        log.info("Responded to quantity update request: productId = {}, quantityState = {}, result = {}",
                request.getProductId(), request.getQuantityState(), result);
        return result;
    }

    @Override
    public boolean deleteProduct(final UUID productId) {
        log.info("Received request to delete product: productId = {}", productId);
        final boolean result = productService.deleteProductById(productId);
        log.info("Responded to product delete request: productId = {}, result = {}", productId, result);
        return result;
    }
}
