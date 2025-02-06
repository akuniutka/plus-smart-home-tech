package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries", schema = "delivery")
@Data
@EqualsAndHashCode(of = "deliveryId")
public class Delivery {

    @Id
    private UUID deliveryId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "country_from")),
            @AttributeOverride(name = "city", column = @Column(name = "city_from")),
            @AttributeOverride(name = "street", column = @Column(name = "street_from")),
            @AttributeOverride(name = "house", column = @Column(name = "house_from")),
            @AttributeOverride(name = "flat", column = @Column(name = "flat_from"))
    })
    private Address fromAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "country_to")),
            @AttributeOverride(name = "city", column = @Column(name = "city_to")),
            @AttributeOverride(name = "street", column = @Column(name = "street_to")),
            @AttributeOverride(name = "house", column = @Column(name = "house_to")),
            @AttributeOverride(name = "flat", column = @Column(name = "flat_to"))
    })
    private Address toAddress;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;
}
