package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.warehouse.model.DeliveryParams;

@Mapper
public interface BookingMapper {

    BookedProductsDto mapToDto(DeliveryParams deliveryParams);
}
