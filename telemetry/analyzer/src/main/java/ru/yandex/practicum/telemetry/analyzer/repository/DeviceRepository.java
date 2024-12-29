package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.model.Device;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, String> {

    List<Device> findByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<Device> findByIdAndHubId(String id, String hubId);
}
