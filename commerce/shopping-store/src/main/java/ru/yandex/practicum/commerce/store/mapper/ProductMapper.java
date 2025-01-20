package ru.yandex.practicum.commerce.store.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.store.model.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    Product mapToEntity(ProductDto dto);

    ProductDto mapToDto(Product product);

    List<ProductDto> mapToDto(List<Product> products);
}
