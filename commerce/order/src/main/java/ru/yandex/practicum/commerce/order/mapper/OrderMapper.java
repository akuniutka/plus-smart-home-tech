package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.List;

@Mapper
public interface OrderMapper {

    OrderDto mapToDto(Order order);

    List<OrderDto> mapToDto(List<Order> orders);
}
