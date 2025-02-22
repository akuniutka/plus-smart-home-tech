package ru.yandex.practicum.telemetry.collector.service.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
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
    private AutoCloseable openMocks;

    @Mock
    private KafkaTemplate<String, SensorEventAvro> mockKafkaTemplate;

    private SensorEventService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        final KafkaTopics kafkaTopics = new KafkaTopics(null, SENSOR_EVENT_TOPIC);
        logListener.startListen();
        logListener.reset();
        service = new SensorEventServiceImpl(kafkaTopics, mockKafkaTemplate);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockKafkaTemplate);
        openMocks.close();
    }

    @Test
    void whenAddToProcessingQueue_ThenCallKafkaSenderWithTopicHubTimestampEventAndLog() throws Exception {

        service.addToProcessingQueue(getTestSensorEventAvro());

        Mockito.verify(mockKafkaTemplate).send(getTestProducerRecord());
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

    private ProducerRecord<String, SensorEventAvro> getTestProducerRecord() {
        return new ProducerRecord<>(
                SENSOR_EVENT_TOPIC,
                null,
                TIMESTAMP.toEpochMilli(),
                HUB_ID,
                getTestSensorEventAvro()
        );
    }
}