package ru.yandex.practicum.telemetry.aggregator.service.sender;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;
import ru.yandex.practicum.telemetry.aggregator.util.LogListener;

import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.ANOTHER_SENSOR_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TEMPERATURE_C;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestEvent;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestSnapshot;
import static ru.yandex.practicum.telemetry.aggregator.util.TestUtils.assertLogs;

class SnapshotSenderImplTest {

    private static final LogListener logListener = new LogListener(SnapshotSenderImpl.class);
    private static final String SNAPSHOT_TOPIC = "test.snapshots";
    private AutoCloseable openMocks;

    @Mock
    private KafkaTemplate<String, SensorSnapshotAvro> mockKafkaTemplate;

    private SnapshotSender snapshotSender;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        final KafkaTopics kafkaTopics = new KafkaTopics(SNAPSHOT_TOPIC);
        logListener.startListen();
        logListener.reset();
        snapshotSender = new SnapshotSenderImpl(kafkaTopics, mockKafkaTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockKafkaTemplate);
        openMocks.close();
    }

    @Test
    void whenSend_ThenPassSnapshotWithParametersAndLog() throws Exception {

        snapshotSender.send(getTestSensorSnapshot());

        Mockito.verify(mockKafkaTemplate).send(getTestProducerRecord());
        assertLogs(logListener.getEvents(), "send.json", getClass());
    }

    private SensorSnapshotAvro getTestSensorSnapshot() {
        return getTestSnapshot(
                HUB_ID,
                getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C),
                getTestEvent(ANOTHER_SENSOR_ID)
        );
    }

    private ProducerRecord<String, SensorSnapshotAvro> getTestProducerRecord() {
        return new ProducerRecord<>(
                SNAPSHOT_TOPIC,
                null,
                NEW_TIMESTAMP.toEpochMilli(),
                HUB_ID,
                getTestSensorSnapshot()
        );
    }
}