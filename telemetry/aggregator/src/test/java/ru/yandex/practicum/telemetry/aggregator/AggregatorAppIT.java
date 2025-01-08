package ru.yandex.practicum.telemetry.aggregator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class AggregatorAppIT {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.2"));

    @DynamicPropertySource
    static void overrideProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.sender.properties.bootstrap.servers", kafka::getBootstrapServers);
        registry.add("kafka.event-listener.properties.bootstrap.servers", kafka::getBootstrapServers);
    }

    @Test
    void whenLoadContext_ThenNoErrors() {
    }
}