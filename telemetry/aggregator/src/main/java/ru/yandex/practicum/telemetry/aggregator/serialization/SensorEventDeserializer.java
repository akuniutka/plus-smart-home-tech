package ru.yandex.practicum.telemetry.aggregator.serialization;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.serialization.BaseAvroDeserializer;

public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
