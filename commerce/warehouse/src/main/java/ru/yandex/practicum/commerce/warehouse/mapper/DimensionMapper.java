package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.warehouse.DimensionDto;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;

@Mapper
public interface DimensionMapper {

    Dimension mapToEntity(DimensionDto dto);
}
