package ru.yandex.practicum.commerce.order.util.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.order.util.UUIDGenerator;

import java.util.UUID;

@Component
public class UUIDGeneratorImpl implements UUIDGenerator {

    @Override
    public UUID getNewUUID() {
        return UUID.randomUUID();
    }
}
