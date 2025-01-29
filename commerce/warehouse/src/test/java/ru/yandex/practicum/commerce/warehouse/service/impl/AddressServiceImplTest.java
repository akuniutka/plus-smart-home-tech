package ru.yandex.practicum.commerce.warehouse.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.warehouse.service.AddressService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddressDtoA;
import static ru.yandex.practicum.commerce.warehouse.util.TestModels.getTestAddressDtoB;

class AddressServiceImplTest {

    private AddressService service;

    @BeforeEach
    void setUp() {
        service = new AddressServiceImpl();
    }

    @Test
    void whenGteAddress_ThenReturnOneOfTwoAddresses() {

        final AddressDto address = service.getAddress();

        assertThat(address, anyOf(equalTo(getTestAddressDtoA()), equalTo(getTestAddressDtoB())));
    }
}