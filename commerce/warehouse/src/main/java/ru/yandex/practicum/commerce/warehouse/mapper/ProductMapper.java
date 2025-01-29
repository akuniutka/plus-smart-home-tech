package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.Product;

@Mapper(uses = DimensionMapper.class)
public interface ProductMapper {

    @Mapping(target = "totalQuantity", ignore = true)
    @Mapping(target = "bookedQuantity", ignore = true)
    Product mapToEntity(NewProductInWarehouseRequest request);
}
