package ru.yandex.practicum.kafka.telemetry.util;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaClientImpl implements KafkaClient {

    private final String bootstrapServers;
    private KafkaSenderImpl sender;

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

    @PreDestroy
    private void close() {
        sender.close();
    }
}
