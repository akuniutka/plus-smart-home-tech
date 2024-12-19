package ru.yandex.practicum.telemetry.router.device.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.Random;

public abstract class AbstractHubDevice {

    protected static final Random random = new Random();

    public abstract void addPayload(HubEventProto.Builder builder);
}
