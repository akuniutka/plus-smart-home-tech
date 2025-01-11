package ru.yandex.practicum.telemetry.analyzer;

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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

@SpringBootTest
@Testcontainers
class AnalyzerAppIT {

    // References to KafkaListener beans to stop them after JUnit runs all the tests. JUnit shuts
    // down Kafka Testcontainers before Spring destroys application context. Between these two
    // events the listeners will signal "Group coordinator is unavailable" if keep running.
    private static KafkaListener<HubEventAvro> hubEventListener;
    private static KafkaListener<SensorSnapshotAvro> snapshotListener;

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.2"));

    @Autowired
    public AnalyzerAppIT(
            final KafkaListener<HubEventAvro> hubEventListener,
            final KafkaListener<SensorSnapshotAvro> snapshotListener
    ) {
        AnalyzerAppIT.hubEventListener = hubEventListener;
        AnalyzerAppIT.snapshotListener = snapshotListener;
    }

    @DynamicPropertySource
    static void overrideProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.hub-event-listener.properties.bootstrap.servers", kafka::getBootstrapServers);
        registry.add("kafka.snapshot-listener.properties.bootstrap.servers", kafka::getBootstrapServers);
    }

    @AfterAll
    static void destroy() {
        if (hubEventListener != null) {
            hubEventListener.stop();
        }
        if (snapshotListener != null) {
            snapshotListener.stop();
        }
    }

    @Test
    void whenLoadContext_ThenNoErrors() {
    }
}