package ru.yandex.practicum.telemetry.collector.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.kafka.telemetry.client.KafkaSender;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaTopics;
import ru.yandex.practicum.telemetry.collector.service.SensorEventService;
import ru.yandex.practicum.telemetry.collector.util.LogListener;

import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.SENSOR_EVENT_TOPIC;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestUtils.assertLogs;

class SensorEventServiceImplTest {

    private static final LogListener logListener = new LogListener(SensorEventServiceImpl.class);
    private static final String SENSOR_ID = "test.light.sensor.1";
    private static final int LINK_QUALITY = 95;
    private static final int LUMINOSITY = 60;
    private KafkaSender kafkaSender;
    private SensorEventService service;

    @BeforeEach
    void setUp() {
        logListener.startListen();
        logListener.reset();
        final KafkaTopics kafkaTopics = new KafkaTopics(null, SENSOR_EVENT_TOPIC);
        kafkaSender = Mockito.mock(KafkaSender.class);
        service = new SensorEventServiceImpl(kafkaTopics, kafkaSender);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(kafkaSender);
    }

    @Test
    void whenAddToProcessingQueue_ThenCallKafkaSenderWithTopicHubTimestampEventAndLog() throws Exception {
        service.addToProcessingQueue(getTestSensorEventAvro());

        Mockito.verify(kafkaSender).send(SENSOR_EVENT_TOPIC, HUB_ID, TIMESTAMP, getTestSensorEventAvro());
        assertLogs(logListener.getEvents(), "add_to_processing_queue.json", getClass());
    }

    private SensorEventAvro getTestSensorEventAvro() {
        return SensorEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(LINK_QUALITY)
                        .setLuminosity(LUMINOSITY)
                        .build())
                .build();
    }
}