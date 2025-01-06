package ru.yandex.practicum.telemetry.collector.mapper.condition.operation;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.telemetry.collector.mapper.factory.OperationMapperImpl;

@Component
public class GreaterThanOperationMapper implements OperationMapperImpl.SpecificOperationMapper {

    @Override
    public ConditionOperationProto getOperationProto() {
        return ConditionOperationProto.GREATER_THAN;
    }

    @Override
    public ConditionOperationAvro getOperationAvro() {
        return ConditionOperationAvro.GREATER_THAN;
    }
}
