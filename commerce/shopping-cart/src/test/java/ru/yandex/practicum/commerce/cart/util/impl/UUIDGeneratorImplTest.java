package ru.yandex.practicum.commerce.cart.util.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.yandex.practicum.commerce.cart.util.UUIDGenerator;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UUIDGeneratorImplTest {

    private static final UUID EXPECTED_UUID = UUID.fromString("7dd41ec5-9a05-4f27-b06d-23b18995724e");
    private final UUIDGenerator uuidGenerator = new UUIDGeneratorImpl();

    @Test
    void whenGetNewUUID_ThenReturnRandomUUIDFromStandardClass() {
        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(EXPECTED_UUID);

            final UUID uuid = uuidGenerator.getNewUUID();

            mockUUID.verify(UUID::randomUUID);
            mockUUID.verifyNoMoreInteractions();
            assertThat(uuid, equalTo(EXPECTED_UUID));
        }
    }
}