package ru.yandex.practicum.telemetry.router.device.sensor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.router.configuration.ValueRange;

import java.util.Random;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbstractSensor {

    protected static final Random random = new Random();
    protected final String id;

    public abstract DeviceTypeProto getType();

    public abstract void addPayload(SensorEventProto.Builder builder);

    protected int getRandomValue(final int currentValue, final int step, final ValueRange valueRange) {
        int newValue = currentValue + random.nextInt(-step, step + 1);
        newValue = Integer.max(valueRange.minValue(), newValue);
        newValue = Integer.min(valueRange.maxValue(), newValue);
        return newValue;
    }

    protected int getMidValue(final ValueRange valueRange) {
        return valueRange.minValue() + (valueRange.maxValue() - valueRange.minValue()) / 2;
    }
}
