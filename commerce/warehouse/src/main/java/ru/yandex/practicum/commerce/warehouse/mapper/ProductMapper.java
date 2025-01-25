package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.Product;

@Mapper(uses = DimensionMapper.class)
public interface ProductMapper {

    Product mapToEntity(NewProductInWarehouseRequest request);
}
