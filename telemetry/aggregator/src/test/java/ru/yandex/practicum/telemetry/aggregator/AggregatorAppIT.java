package ru.yandex.practicum.telemetry.aggregator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@SpringBootTest
@Testcontainers
class AggregatorAppIT {

    // Reference to KafkaListener bean to stop it after JUnit runs all the tests. JUnit shuts
    // down Kafka Testcontainers before Spring destroys application context. Between these two
    // events the listener will signal "Group coordinator is unavailable" if keeps running.
    private static KafkaListener<SensorEventAvro> sensorEventListener;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.2"));

    @Autowired
    public AggregatorAppIT(final KafkaListener<SensorEventAvro> sensorEventListener) {
        AggregatorAppIT.sensorEventListener = sensorEventListener;
    }

    @DynamicPropertySource
    static void overrideProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.sender.properties.bootstrap.servers", kafka::getBootstrapServers);
        registry.add("kafka.event-listener.properties.bootstrap.servers", kafka::getBootstrapServers);
    }

    @AfterAll
    static void destroy() {
        if (sensorEventListener != null) {
            sensorEventListener.stop();
        }
    }

    @Test
    void whenLoadContext_ThenNoErrors() {
    }
}