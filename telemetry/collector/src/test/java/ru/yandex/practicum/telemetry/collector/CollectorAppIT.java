package ru.yandex.practicum.telemetry.collector;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.mapper.ActionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.DeviceTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.HubEventMapperFactory;
import ru.yandex.practicum.telemetry.collector.mapper.OperationMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.telemetry.collector.mapper.SensorEventMapperFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class CollectorAppIT {

    @Autowired
    private HubEventMapperFactory hubEventMapperFactory;

    @Autowired
    private SensorEventMapperFactory sensorEventMapperFactory;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private ConditionMapperFactory conditionMapperFactory;

    @Autowired
    private ConditionTypeMapper conditionTypeMapper;

    @Autowired
    private OperationMapper operationMapper;

    @Autowired
    private ActionTypeMapper actionTypeMapper;

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleHubEventPayloadTypes")
    void whenGetHubEventMapper_ThenRequiredBeanExist(final HubEventProto.PayloadCase payloadType) {
        final HubEventMapper mapper = hubEventMapperFactory.getMapper(payloadType);

        assertThat(mapper, notNullValue());
        assertThat(mapper.getPayloadType(), equalTo(payloadType));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleSensorEventPayloadTypes")
    void whenGetSensorEventMapper_ThenReturnRequiredBean(final SensorEventProto.PayloadCase payloadType) {
        final SensorEventMapper mapper = sensorEventMapperFactory.getMapper(payloadType);

        assertThat(mapper, notNullValue());
        assertThat(mapper.getPayloadType(), equalTo(payloadType));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleDeviceTypes")
    void whenMapDeviceTypeToAvro_ThenRequiredMapperExist(final DeviceTypeProto deviceType) {
        assertDoesNotThrow(() -> deviceTypeMapper.mapToAvro(deviceType));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleConditionValueTypes")
    void whenGetConditionMapper_ThenReturnRequiredBean(final ScenarioConditionProto.ValueCase valueType) {
        final ConditionMapper mapper = conditionMapperFactory.getMapper(valueType);

        assertThat(mapper, notNullValue());
        assertThat(mapper.getValueType(), equalTo(valueType));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleConditionTypes")
    void whenMapConditionTypeToAvro_ThenRequiredMapperExist(final ConditionTypeProto conditionType) {
        assertDoesNotThrow(() -> conditionTypeMapper.mapToAvro(conditionType));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleOperations")
    void whenMapOperationToAvro_ThenRequiredMapperExist(final ConditionOperationProto operation) {
        assertDoesNotThrow(() -> operationMapper.mapToAvro(operation));
    }

    @ParameterizedTest
    @MethodSource("ru.yandex.practicum.telemetry.collector.util.TestModels#getPossibleActionTypes")
    void whenMapActionTypeToAvro_ThenRequiredMapperExist(final ActionTypeProto actionType) {
        assertDoesNotThrow(() -> actionTypeMapper.mapToAvro(actionType));
    }
}
