package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.warehouse.model.BookedProducts;

@Mapper
public interface BookedProductsMapper {

    BookedProductsDto mapToDto(BookedProducts bookedProducts);
}
