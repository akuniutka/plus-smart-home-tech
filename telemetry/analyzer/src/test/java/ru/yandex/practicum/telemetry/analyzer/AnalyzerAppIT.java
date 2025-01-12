package ru.yandex.practicum.telemetry.analyzer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.analyzer.handler.HubEventHandlerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
    private HubEventHandlerFactory hubEventHandlerFactory;

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

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.analyzer.util.TestModels#getPossibleHubEventPayloadTypes")
    void whenGetHubEventHandler_ThenRequiredBeanExist(final String payloadType) {
        final HubEventHandler handler = hubEventHandlerFactory.getHandler(payloadType);

        assertThat(handler, notNullValue());
        assertThat(handler.getPayloadType(), equalTo(payloadType));
    }
}