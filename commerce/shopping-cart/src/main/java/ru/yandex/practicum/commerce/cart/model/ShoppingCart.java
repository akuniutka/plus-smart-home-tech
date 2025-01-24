package ru.yandex.practicum.commerce.cart.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts", schema = "cart")
@Data
@EqualsAndHashCode(of = "shoppingCartId")
public class ShoppingCart {

    @Id
    private UUID shoppingCartId;

    private String username;

    @Enumerated(EnumType.STRING)
    private ShoppingCartState shoppingCartState;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shopping_cart_items", schema = "cart",
            joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();
}
