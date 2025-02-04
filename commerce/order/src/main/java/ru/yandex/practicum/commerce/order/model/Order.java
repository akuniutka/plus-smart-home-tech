package ru.yandex.practicum.commerce.order.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.commerce.dto.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "order_")
@Data
@EqualsAndHashCode(of = "orderId")
public class Order {

    @Id
    private UUID orderId;

    private String username;
    private UUID shoppingCartId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", schema = "order_",
    joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    private UUID paymentId;
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @Embedded
    private Address deliveryAddress;

    private BigDecimal deliveryWeight;
    private BigDecimal deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
}
