package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;

@Mapper
public interface DeliveryMapper {

    Delivery mapToEntity(DeliveryDto dto);

    DeliveryDto mapToDto(Delivery delivery);
}
