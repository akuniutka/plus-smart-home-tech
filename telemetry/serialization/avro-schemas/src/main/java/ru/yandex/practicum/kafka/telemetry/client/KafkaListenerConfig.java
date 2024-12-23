package ru.yandex.practicum.kafka.telemetry.client;

import java.util.List;
import java.util.Properties;

public interface KafkaListenerConfig {

    List<String> topics();

    long pollTimeout();

    int commitBatchSize();

    Properties properties();
}
