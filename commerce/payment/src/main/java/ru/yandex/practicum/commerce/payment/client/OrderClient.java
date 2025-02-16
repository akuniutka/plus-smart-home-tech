package ru.yandex.practicum.commerce.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.client.OrderOperations;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderOperations {

}
