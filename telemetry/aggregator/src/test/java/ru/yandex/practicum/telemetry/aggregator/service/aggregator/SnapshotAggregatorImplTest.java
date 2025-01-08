package ru.yandex.practicum.telemetry.aggregator.service.aggregator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotRepository;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregator;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotSender;
import ru.yandex.practicum.telemetry.aggregator.util.LogListener;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.ANOTHER_SENSOR_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.PAST_TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.TEMPERATURE_C;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TEMPERATURE_C;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestSnapshot;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestEvent;
import static ru.yandex.practicum.telemetry.aggregator.util.TestUtils.assertLogs;

class SnapshotAggregatorImplTest {

    private static final LogListener logListener = new LogListener(SnapshotAggregatorImpl.class);
    private SnapshotRepository mockRepository;
    private SnapshotSender mockSnapshotSender;
    private InOrder inOrder;

    private SnapshotAggregator aggregator;

    @BeforeEach
    void setUp() {
        final Clock clock = Clock.fixed(NEW_TIMESTAMP, ZoneId.of("Z"));
        mockRepository = Mockito.mock(SnapshotRepository.class);
        mockSnapshotSender = Mockito.mock(SnapshotSender.class);
        inOrder = Mockito.inOrder(mockRepository, mockSnapshotSender);
        logListener.startListen();
        logListener.reset();
        aggregator = new SnapshotAggregatorImpl(clock, mockRepository, mockSnapshotSender);
    }

    @AfterEach
    void tearDown() {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, mockSnapshotSender);
    }

    @Test
    void whenAggregateAndFirstEventForHub_ThenSaveNewSnapshotAndSendItAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.empty());
        doNothing().when(mockRepository).save(any());
        doNothing().when(mockSnapshotSender).send(any());

        aggregator.aggregate(getTestEvent(TIMESTAMP, TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        inOrder.verify(mockRepository).save(getTestSnapshot(HUB_ID, getTestEvent(TIMESTAMP, TEMPERATURE_C)));
        inOrder.verify(mockSnapshotSender).send(getTestSnapshot(HUB_ID,
                getTestEvent(TIMESTAMP, TEMPERATURE_C)));
        assertLogs(logListener.getEvents(), "first_event_for_sensor.json", getClass());
    }

    @Test
    void whenAggregateAndFirstEventForSensor_ThenUpdateSnapshotAndSendItAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(ANOTHER_SENSOR_ID))));
        doNothing().when(mockRepository).save(any());
        doNothing().when(mockSnapshotSender).send(any());

        aggregator.aggregate(getTestEvent(TIMESTAMP, TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        inOrder.verify(mockRepository).save(getTestSnapshot(HUB_ID, getTestEvent(ANOTHER_SENSOR_ID),
                getTestEvent(TIMESTAMP, TEMPERATURE_C)));
        inOrder.verify(mockSnapshotSender).send(getTestSnapshot(HUB_ID, getTestEvent(ANOTHER_SENSOR_ID),
                getTestEvent(TIMESTAMP, TEMPERATURE_C)));
        assertLogs(logListener.getEvents(), "first_event_for_sensor.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithTimestampInPast_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(PAST_TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_timestamp_in_past.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithTimestampInPastAndNotSingleSensor_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(ANOTHER_SENSOR_ID), getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(PAST_TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_timestamp_in_past.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithExistingTimestamp_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_existing_timestamp.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithExistingTimestampAndNotSingleSensor_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(ANOTHER_SENSOR_ID), getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_existing_timestamp.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithNewTimestampAndSameData_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(NEW_TIMESTAMP, TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_new_timestamp_and_same_data.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithNewTimestampAndSameDataAndNotSingleSensor_ThenSkipEventAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(ANOTHER_SENSOR_ID), getTestEvent(TIMESTAMP, TEMPERATURE_C))));

        aggregator.aggregate(getTestEvent(NEW_TIMESTAMP, TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        assertLogs(logListener.getEvents(), "event_with_new_timestamp_and_same_data.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithNewTimestampAndNewData_ThenUpdateSnapshotAndSendItAndLog() throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(TIMESTAMP, TEMPERATURE_C))));
        doNothing().when(mockRepository).save(any());
        doNothing().when(mockSnapshotSender).send(any());

        aggregator.aggregate(getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        inOrder.verify(mockRepository).save(getTestSnapshot(HUB_ID, getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));
        inOrder.verify(mockSnapshotSender).send(getTestSnapshot(HUB_ID,
                getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));
        assertLogs(logListener.getEvents(), "event_with_new_timestamp_and_new_data.json", getClass());
    }

    @Test
    void whenAggregateAndEventWithNewTimestampAndNewDataANdNotSingleSensor_ThenUpdateSnapshotAndSendItAndLog()
            throws Exception {
        when(mockRepository.findByHubId(any())).thenReturn(Optional.of(getTestSnapshot(HUB_ID,
                getTestEvent(ANOTHER_SENSOR_ID), getTestEvent(TIMESTAMP, TEMPERATURE_C))));
        doNothing().when(mockRepository).save(any());
        doNothing().when(mockSnapshotSender).send(any());

        aggregator.aggregate(getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C));

        inOrder.verify(mockRepository).findByHubId(HUB_ID);
        inOrder.verify(mockRepository).save(getTestSnapshot(HUB_ID, getTestEvent(ANOTHER_SENSOR_ID),
                getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));
        inOrder.verify(mockSnapshotSender).send(getTestSnapshot(HUB_ID, getTestEvent(ANOTHER_SENSOR_ID),
                getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));
        assertLogs(logListener.getEvents(), "event_with_new_timestamp_and_new_data.json", getClass());
    }
}