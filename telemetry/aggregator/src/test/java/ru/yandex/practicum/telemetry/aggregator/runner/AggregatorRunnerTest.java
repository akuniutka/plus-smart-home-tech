package ru.yandex.practicum.telemetry.aggregator.runner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListener;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

class AggregatorRunnerTest {

    private AutoCloseable openMocks;

    @Mock
    private ApplicationArguments applicationArguments;

    @Mock
    private KafkaListener<SensorEventAvro> mockListener;

    private AggregatorRunner runner;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        runner = new AggregatorRunner(mockListener);
    }

    @AfterEach
    void tearDown() throws Exception {
        Mockito.verifyNoMoreInteractions(applicationArguments, mockListener);
        openMocks.close();
    }

    @Test
    void whenRun_ThenStartListener() {
        runner.run(applicationArguments);

        Mockito.verify(mockListener).start();
    }
}