package ru.yandex.practicum.commerce.cart.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCartDto mapToDto(ShoppingCart shoppingCart);
}
