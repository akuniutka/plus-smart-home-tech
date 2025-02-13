package ru.yandex.practicum.commerce.warehouse.util.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.warehouse.util.UUIDGenerator;

import java.util.UUID;

@Component
public class UUIDGeneratorImpl implements UUIDGenerator {

    @Override
    public UUID getNewUUID() {
        return UUID.randomUUID();
    }
}
