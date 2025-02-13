package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_bookings", schema = "warehouse")
@Data
@EqualsAndHashCode(of = "orderBookingId")
public class OrderBooking {

    @Id
    private UUID orderBookingId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_booking_items", schema = "warehouse",
            joinColumns = @JoinColumn(name = "order_booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    private UUID orderId;
    private UUID deliveryId;
}
