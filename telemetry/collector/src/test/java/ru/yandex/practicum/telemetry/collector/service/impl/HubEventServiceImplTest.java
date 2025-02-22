package ru.yandex.practicum.telemetry.collector.service.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.HubEventService;
import ru.yandex.practicum.telemetry.collector.util.LogListener;

import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_EVENT_TOPIC;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestUtils.assertLogs;

class HubEventServiceImplTest {

    private static final LogListener logListener = new LogListener(HubEventServiceImpl.class);
    private static final String SENSOR_ID = "test.light.sensor.1";
    private AutoCloseable openMocks;

    @Mock
    private KafkaTemplate<String, HubEventAvro> mockKafkaTemplate;

    private HubEventService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        logListener.startListen();
        logListener.reset();
        final KafkaTopics kafkaTopics = new KafkaTopics(HUB_EVENT_TOPIC, null);
        service = new HubEventServiceImpl(kafkaTopics, mockKafkaTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockKafkaTemplate);
        openMocks.close();
    }

    @Test
    void whenAddToProcessingQueue_ThenCallKafkaSenderWithTopicHubTimestampEventAndLog() throws Exception {
        service.addToProcessingQueue(getTestHubEventAvro());

        Mockito.verify(mockKafkaTemplate).send(getTestProducerRecord());
        assertLogs(logListener.getEvents(), "add_to_processing_queue.json", getClass());
    }

    private HubEventAvro getTestHubEventAvro() {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(SENSOR_ID)
                        .build())
                .build();
    }

    private ProducerRecord<String, HubEventAvro> getTestProducerRecord() {
        return new ProducerRecord<>(
                HUB_EVENT_TOPIC,
                null,
                TIMESTAMP.toEpochMilli(),
                HUB_ID,
                getTestHubEventAvro()
        );
    }
}