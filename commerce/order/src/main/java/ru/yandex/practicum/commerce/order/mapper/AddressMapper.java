package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;
import ru.yandex.practicum.commerce.order.model.Address;

@Mapper
public interface AddressMapper {

    Address mapToEntity(AddressDto dto);
}
