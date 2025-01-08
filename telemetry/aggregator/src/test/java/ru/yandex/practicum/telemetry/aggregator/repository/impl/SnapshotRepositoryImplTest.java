package ru.yandex.practicum.telemetry.aggregator.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.ANOTHER_SENSOR_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TEMPERATURE_C;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.NEW_TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.TEMPERATURE_C;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestSnapshot;
import static ru.yandex.practicum.telemetry.aggregator.util.TestModels.getTestEvent;

class SnapshotRepositoryImplTest {

    private static final String ANOTHER_HUB_ID = "test.hub.2";
    private SnapshotRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SnapshotRepositoryImpl();
    }

    @Test
    void whenFindByHubIdAndRepositoryEmpty_ThenReturnEmptyOptional() {
        final Optional<SensorSnapshotAvro> snapshot = repository.findByHubId(HUB_ID);

        assertThat(snapshot.isEmpty(), is(true));
    }

    @Test
    void whenFindByHubIdAndSnapshotNotExist_ThenReturnEmptyOptional() {
        repository.save(getTestSnapshot(HUB_ID, getTestEvent(TIMESTAMP, TEMPERATURE_C)));

        final Optional<SensorSnapshotAvro> snapshot = repository.findByHubId(ANOTHER_HUB_ID);

        assertThat(snapshot.isEmpty(), is(true));
    }

    @Test
    void whenFindByHubIdAndSnapshotExist_ThanReturnIt() {
        repository.save(getTestSnapshot(HUB_ID, getTestEvent(TIMESTAMP, TEMPERATURE_C)));

        final Optional<SensorSnapshotAvro> snapshot = repository.findByHubId(HUB_ID);

        assertThat(snapshot.isEmpty(), is(false));
        assertThat(snapshot.get(), equalTo(getTestSnapshot(HUB_ID, getTestEvent(TIMESTAMP, TEMPERATURE_C))));
    }

    @Test
    void whenSaveSnapshotWithDifferentHubId_ThenItNotOverwriteOthers() {
        repository.save(getTestSnapshot(HUB_ID, getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));
        repository.save(getTestSnapshot(ANOTHER_HUB_ID, getTestEvent(ANOTHER_SENSOR_ID)));

        final Optional<SensorSnapshotAvro> snapshot = repository.findByHubId(HUB_ID);
        final Optional<SensorSnapshotAvro> anotherSnapshot = repository.findByHubId(ANOTHER_HUB_ID);

        assertThat(snapshot.isEmpty(), is(false));
        assertThat(snapshot.get(),
                equalTo(getTestSnapshot(HUB_ID, getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C))));
        assertThat(anotherSnapshot.isEmpty(), is(false));
        assertThat(anotherSnapshot.get(), equalTo(getTestSnapshot(ANOTHER_HUB_ID, getTestEvent(ANOTHER_SENSOR_ID))));
    }

    @Test
    void whenSaveSnapshotWithSameHubId_ThenItOverwritePreviousVersion() {
        repository.save(getTestSnapshot(HUB_ID, getTestEvent(TIMESTAMP, TEMPERATURE_C)));
        repository.save(getTestSnapshot(HUB_ID, getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C)));

        final Optional<SensorSnapshotAvro> snapshot = repository.findByHubId(HUB_ID);

        assertThat(snapshot.isEmpty(), is(false));
        assertThat(snapshot.get(), equalTo(getTestSnapshot(HUB_ID, getTestEvent(NEW_TIMESTAMP, NEW_TEMPERATURE_C))));
    }

}