package ru.yandex.practicum.telemetry.aggregator.service.sender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;
import ru.yandex.practicum.telemetry.aggregator.util.LogListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.eq;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestUtils.assertLogs;

class SnapshotSenderImplTest {

    private static final LogListener logListener = new LogListener(SnapshotSenderImpl.class);
    private static final String SNAPSHOT_TOPIC = "test.snapshots";
    private KafkaSender mockKafkaSender;
    private SensorSnapshotAvro mockSnapshot;
    private ArgumentCaptor<SensorSnapshotAvro> snapshotCaptor;

    private SnapshotSender snapshotSender;

    @BeforeEach
    void setUp() {
        final KafkaTopics kafkaTopics = new KafkaTopics(SNAPSHOT_TOPIC);
        mockKafkaSender = Mockito.mock(KafkaSender.class);
        mockSnapshot = Mockito.mock(SensorSnapshotAvro.class);
        snapshotCaptor = ArgumentCaptor.forClass(SensorSnapshotAvro.class);
        logListener.startListen();
        logListener.reset();
        snapshotSender = new SnapshotSenderImpl(kafkaTopics, mockKafkaSender);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockKafkaSender, mockSnapshot);
    }

    @Test
    void whenSend_ThenPassSnapshotWithParametersAndLog() throws Exception {
        Mockito.when(mockSnapshot.getHubId()).thenReturn(HUB_ID);
        Mockito.when(mockSnapshot.getTimestamp()).thenReturn(TIMESTAMP);

        snapshotSender.send(mockSnapshot);

        Mockito.verify(mockSnapshot, Mockito.times(2)).getHubId();
        Mockito.verify(mockSnapshot, Mockito.times(2)).getTimestamp();
        Mockito.verify(mockKafkaSender).send(eq(SNAPSHOT_TOPIC), eq(HUB_ID), eq(TIMESTAMP), snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue(), sameInstance(mockSnapshot));
        assertLogs(logListener.getEvents(), "send.json", getClass());
    }
}