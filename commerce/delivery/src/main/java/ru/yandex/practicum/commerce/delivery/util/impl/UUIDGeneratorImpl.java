package ru.yandex.practicum.commerce.delivery.util.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.delivery.util.UUIDGenerator;

import java.util.UUID;

@Component
public class UUIDGeneratorImpl implements UUIDGenerator {

    @Override
    public UUID getNewUUID() {
        return UUID.randomUUID();
    }
}
