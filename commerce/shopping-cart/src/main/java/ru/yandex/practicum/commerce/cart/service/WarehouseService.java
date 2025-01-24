package ru.yandex.practicum.commerce.cart.service;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.service.WarehouseOperations;

@FeignClient(name = "warehouse")
public interface WarehouseService extends WarehouseOperations {

}
