package ru.yandex.practicum.commerce.client;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.commerce.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.store.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderOperations {

    @PutMapping
    OrderDto createOrder(@RequestParam String username, @RequestBody @Valid CreateNewOrderRequest request);

    @GetMapping("/{orderId}")
    OrderDto getOrderById(@PathVariable UUID orderId);

    @GetMapping
    List<OrderDto> getOrdersByUsername(@RequestParam String username, Pageable pageable);

    @PostMapping("/calculate/product")
    OrderDto calculateProductCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto confirmAssembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto setAssemblyFailed(@RequestBody UUID orderId);

    @PostMapping("/payment")
    OrderDto confirmPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto setPaymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto confirmDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto setDeliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/return")
    OrderDto returnProducts(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody UUID orderId);
}
