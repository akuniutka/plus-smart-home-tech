package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.payment.model.Payment;

@Mapper
public interface PaymentMapper {

    PaymentDto mapToDto(Payment payment);
}
