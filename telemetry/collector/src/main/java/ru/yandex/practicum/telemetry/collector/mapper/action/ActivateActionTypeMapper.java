package ru.yandex.practicum.telemetry.collector.mapper.action;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.telemetry.collector.mapper.factory.ActionTypeMapperImpl;

@Component
public class ActivateActionTypeMapper implements ActionTypeMapperImpl.SpecificActionTypeMapper {

    @Override
    public ActionTypeProto getActionTypeProto() {
        return ActionTypeProto.ACTIVATE;
    }

    @Override
    public ActionTypeAvro getActionTypeAvro() {
        return ActionTypeAvro.ACTIVATE;
    }
}
