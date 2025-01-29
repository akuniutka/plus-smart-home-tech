package ru.yandex.practicum.commerce.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    public ProductDto addProduct(@RequestBody @Valid final ProductDto newProductDto) {
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

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable final UUID productId) {
        log.info("Received request for product: productId = {}", productId);
        final Product product = productService.getProductById(productId);
        final ProductDto dto = productMapper.mapToDto(product);
        log.info("Responded with requested product: productId = {}, productName = {}", dto.getProductId(),
                dto.getProductName());
        log.debug("Requested product = {}", dto);
        return dto;
    }

    @GetMapping
    public List<ProductDto> findProductsByCategory(@RequestParam final ProductCategory category, final Pageable pageable) {
        log.info("Received request for products in category: category = {}", category);
        log.debug("Requested page = {}, page size = {}, sort by {}", pageable.getPage(), pageable.getSize(),
                pageable.getSort());
        final PageRequest page = PageRequest.of(pageable.getPage(), pageable.getSize(), Sort.by(pageable.getSort()));
        final List<Product> products = productService.findProductsByCategory(category, page);
        final List<ProductDto> dtos = productMapper.mapToDto(products);
        log.info("Responded with products in category: category = {}", category);
        log.debug("Requested products = {}", dtos);
        return dtos;
    }

    @PutMapping
    public ProductDto updateProduct(@RequestBody @Valid final ProductDto updatedProductDto) {
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

    @PostMapping("/quantityState")
    public boolean setProductQuantity(@Valid final SetProductQuantityStateRequest request) {
        log.info("Received request to update product quantity: productId = {}, quantityState = {}",
                request.getProductId(), request.getQuantityState());
        final boolean result = productService.setProductQuantity(request);
        log.info("Responded to quantity update request: productId = {}, quantityState = {}, result = {}",
                request.getProductId(), request.getQuantityState(), result);
        return result;
    }

    @PostMapping("/removeProductFromStore")
    public boolean deleteProduct(@RequestBody final UUID productId) {
        log.info("Received request to delete product: productId = {}", productId);
        final boolean result = productService.deleteProductById(productId);
        log.info("Responded to product delete request: productId = {}, result = {}", productId, result);
        return result;
    }
}
