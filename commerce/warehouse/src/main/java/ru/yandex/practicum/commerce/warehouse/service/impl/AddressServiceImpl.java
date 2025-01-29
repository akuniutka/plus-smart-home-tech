package ru.yandex.practicum.commerce.warehouse.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class AddressServiceImpl implements AddressService {

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(2)];

    @Override
    public AddressDto getAddress() {
        final AddressDto address = new AddressDto();
        address.setCountry(CURRENT_ADDRESS);
        address.setCity(CURRENT_ADDRESS);
        address.setStreet(CURRENT_ADDRESS);
        address.setHouse(CURRENT_ADDRESS);
        address.setFlat(CURRENT_ADDRESS);
        return address;
    }
}
