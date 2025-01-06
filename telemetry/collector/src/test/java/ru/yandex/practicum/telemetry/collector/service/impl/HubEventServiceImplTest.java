package ru.yandex.practicum.telemetry.collector.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
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
    private KafkaSender kafkaSender;
    private HubEventService service;

    @BeforeEach
    void setUp() {
        logListener.startListen();
        logListener.reset();
        final KafkaTopics kafkaTopics = new KafkaTopics(HUB_EVENT_TOPIC, null);
        kafkaSender = Mockito.mock(KafkaSender.class);
        service = new HubEventServiceImpl(kafkaTopics, kafkaSender);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(kafkaSender);
    }

    @Test
    void whenAddToProcessingQueue_ThenCallKafkaSenderWithTopicHubTimestampEventAndLog() throws Exception {
        service.addToProcessingQueue(getTestHubEventAvro());

        Mockito.verify(kafkaSender).send(HUB_EVENT_TOPIC, HUB_ID, TIMESTAMP, getTestHubEventAvro());
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
}