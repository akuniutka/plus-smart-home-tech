package ru.yandex.practicum.kafka.telemetry.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaClientImpl implements KafkaClient {

    private final String bootstrapServers;
    private KafkaSender sender;

    public KafkaClientImpl(@Value("${kafka.bootstrap-servers:localhost:9092}") final String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public KafkaSender getSender() {
        if (sender == null) {
            synchronized (this) {
                if (sender == null) {
                    sender = new KafkaSenderImpl(bootstrapServers);
                }
            }
        }
        return sender;
    }
}
