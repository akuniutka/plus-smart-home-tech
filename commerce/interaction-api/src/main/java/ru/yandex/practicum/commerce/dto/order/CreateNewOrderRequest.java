package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.delivery.AddressDto;

@Data
public class CreateNewOrderRequest {

    @NotNull
    @Valid
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}
