package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Address {

    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
