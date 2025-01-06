package ru.yandex.practicum.telemetry.collector.util;

import com.google.protobuf.Timestamp;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public final class TestModels {

    public static final String HUB_EVENT_TOPIC = "test.hub.events";
    public static final String SENSOR_EVENT_TOPIC = "test.sensor.events";
    public static final String HUB_ID = "test.hub.1";
    public static final String SENSOR_ID = "test.light.sensor.1";
    public static final String SCENARIO_NAME = "test scenario";
    public static final Instant TIMESTAMP = Instant.parse("2000-01-31T13:30:55.123Z");
    public static final Timestamp TIMESTAMP_PROTO = Timestamp.newBuilder()
            .setSeconds(TIMESTAMP.getEpochSecond())
            .setNanos(TIMESTAMP.getNano())
            .build();
    public static final int INT_VALUE = 100;

    private TestModels() {
    }

    public static HubEventProto getTestHubEventProtoWithNoPayload() {
        return HubEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .build();
    }

    public static SensorEventProto getTestSensorEventProtoWithNoPayload() {
        return SensorEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setId(SENSOR_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .build();
    }

    public static ScenarioConditionProto getTestScenarioConditionProto(
            final ConditionTypeProto conditionType,
            final ConditionOperationProto operation,
            final Consumer<ScenarioConditionProto.Builder> valueProvider
    ) {
        final ScenarioConditionProto.Builder conditionBuilder = ScenarioConditionProto.newBuilder()
                .setSensorId(SENSOR_ID)
                .setType(conditionType)
                .setOperation(operation);
        valueProvider.accept(conditionBuilder);
        return conditionBuilder.build();
    }

    public static ScenarioConditionAvro getTestScenarioConditionAvro(
            final ConditionTypeAvro conditionType,
            final ConditionOperationAvro operation,
            final Consumer<ScenarioConditionAvro.Builder> valueProvider
    ) {
        final ScenarioConditionAvro.Builder conditionBuilder = ScenarioConditionAvro.newBuilder()
                .setSensorId(SENSOR_ID)
                .setType(conditionType)
                .setOperation(operation);
        valueProvider.accept(conditionBuilder);
        return conditionBuilder.build();
    }

    public static List<HubEventProto.PayloadCase> getPossibleHubEventPayloadTypes() {
        return Arrays.stream(HubEventProto.PayloadCase.values())
                .filter(payloadType -> payloadType != HubEventProto.PayloadCase.PAYLOAD_NOT_SET)
                .toList();
    }

    public static List<SensorEventProto.PayloadCase> getPossibleSensorEventPayloadTypes() {
        return Arrays.stream(SensorEventProto.PayloadCase.values())
                .filter((payloadType -> payloadType != SensorEventProto.PayloadCase.PAYLOAD_NOT_SET))
                .toList();
    }

    public static List<DeviceTypeProto> getPossibleDeviceTypes() {
        return Arrays.stream(DeviceTypeProto.values())
                .filter(deviceType -> deviceType != DeviceTypeProto.UNRECOGNIZED)
                .toList();
    }

    public static List<ScenarioConditionProto.ValueCase> getPossibleConditionValueTypes() {
        return Arrays.asList(ScenarioConditionProto.ValueCase.values());
    }

    public static List<ConditionTypeProto> getPossibleConditionTypes() {
        return Arrays.stream(ConditionTypeProto.values())
                .filter(conditionType -> conditionType != ConditionTypeProto.UNRECOGNIZED)
                .toList();
    }

    public static List<ConditionOperationProto> getPossibleOperations() {
        return Arrays.stream(ConditionOperationProto.values())
                .filter(operationType -> operationType != ConditionOperationProto.UNRECOGNIZED)
                .toList();
    }

    public static List<ActionTypeProto> getPossibleActionTypes() {
        return Arrays.stream(ActionTypeProto.values())
                .filter(actionType -> actionType != ActionTypeProto.UNRECOGNIZED)
                .toList();
    }

    /**
     * Creates a list of <code>ConditionTypeAvro</code> of length to pair to list of allowed
     * <code>ConditionTypeProto</code>.
     *
     * @return list of <code>ConditionTypeAvro</code> of length required for tests
     */
    public static List<ConditionTypeAvro> getTestConditionTypesAvro() {
        final ConditionTypeAvro[] values = ConditionTypeAvro.values();
        final int size = getPossibleConditionTypes().size();
        return IntStream.range(0, size)
                .mapToObj(i -> values[i % values.length])
                .toList();
    }

    /**
     * Creates a list of <code>ConditionOperationAvro</code> of length to pair to list of allowed
     * <code>ConditionOperationProto</code>.
     *
     * @return list of <code>ConditionOperationAvro</code> of length required for tests
     */
    public static List<ConditionOperationAvro> getTestOperationsAvro() {
        final ConditionOperationAvro[] values = ConditionOperationAvro.values();
        final int size = getPossibleOperations().size();
        return IntStream.range(0, size)
                .mapToObj(i -> values[i % values.length])
                .toList();
    }

    /**
     * Creates a list of <code>ActionTypeAvro</code> of length to pair to list of allowed <code>ActionTypeProto</code>.
     *
     * @return list of <code>ActionTypeAvro</code> of length required for tests
     */
    public static List<ActionTypeAvro> getTestActionTypesAvro() {
        final ActionTypeAvro[] values = ActionTypeAvro.values();
        final int size = getPossibleActionTypes().size();
        return IntStream.range(0, size)
                .mapToObj(i -> values[i % values.length])
                .toList();
    }
}
