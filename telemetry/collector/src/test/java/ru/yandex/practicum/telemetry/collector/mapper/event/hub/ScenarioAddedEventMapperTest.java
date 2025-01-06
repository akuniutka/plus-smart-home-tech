package ru.yandex.practicum.telemetry.collector.mapper.event.hub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.mapper.ActionTypeMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapper;
import ru.yandex.practicum.telemetry.collector.mapper.ConditionMapperFactory;
import ru.yandex.practicum.telemetry.collector.util.TestModels;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.HUB_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.INT_VALUE;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.SCENARIO_NAME;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.SENSOR_ID;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.TIMESTAMP_PROTO;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getPossibleConditionTypes;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getPossibleOperations;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestConditionTypesAvro;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestHubEventProtoWithNoPayload;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestOperationsAvro;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestScenarioConditionAvro;
import static ru.yandex.practicum.telemetry.collector.util.TestModels.getTestScenarioConditionProto;

class ScenarioAddedEventMapperTest {

    private int conditionCount;
    private List<ScenarioConditionProto> conditionsProto;
    //    private List<ScenarioConditionAvro> conditionsAvro;
    private List<ActionTypeAvro> actionTypesAvro;
    private List<ActionTypeProto> actionTypesProto;
    private ConditionMapperFactory mockConditionMapperFactory;
    private ConditionMapper mockConditionMapper;
    private ActionTypeMapper mockActionTypeMapper;

    private ScenarioAddedEventMapper mapper;

    @BeforeEach
    void setUp() {
        conditionsProto = getTestConditionsProto();
        conditionCount = conditionsProto.size();
        final List<ScenarioConditionAvro> conditionsAvro = getTestConditionsAvro();
        actionTypesProto = TestModels.getPossibleActionTypes();
        actionTypesAvro = TestModels.getTestActionTypesAvro();
        mockConditionMapperFactory = Mockito.mock(ConditionMapperFactory.class);
        mockConditionMapper = Mockito.mock(ConditionMapper.class);
        mockActionTypeMapper = Mockito.mock(ActionTypeMapper.class);
        when(mockConditionMapperFactory.getMapper(any())).thenReturn(mockConditionMapper);
        for (int i = 0; i < conditionCount; i++) {
            when(mockConditionMapper.mapToAvro(conditionsProto.get(i))).thenReturn(conditionsAvro.get(i));
        }
        for (int i = 0; i < actionTypesProto.size(); i++) {
            when(mockActionTypeMapper.mapToAvro(actionTypesProto.get(i))).thenReturn(actionTypesAvro.get(i));
        }
        mapper = new ScenarioAddedEventMapper(mockConditionMapperFactory, mockActionTypeMapper);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(mockConditionMapperFactory, mockConditionMapper, mockActionTypeMapper);
    }

    @Test
    void whenGetPayloadType_ThenReturnScenarioAdded() {
        final HubEventProto.PayloadCase payloadType = mapper.getPayloadType();

        assertThat(payloadType, equalTo(HubEventProto.PayloadCase.SCENARIO_ADDED));
    }

    @Test
    void whenMapToAvro_ThenReturnCorrectHubEventAvro() {
        final HubEventAvro event = mapper.mapToAvro(getTestHubEventProto());

        verify(mockConditionMapperFactory, times(conditionCount)).getMapper(ScenarioConditionProto.ValueCase.INT_VALUE);
        conditionsProto.forEach(condition -> Mockito.verify(mockConditionMapper).mapToAvro(condition));
        actionTypesProto.forEach(actionType -> Mockito.verify(mockActionTypeMapper).mapToAvro(actionType));
        assertThat(event, equalTo(getTestHubEventAvro()));
    }

    @Test
    void whenMapToAvroAndWrongPayloadType_ThenThrowException() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> mapper.mapToAvro(getTestHubEventProtoWithNoPayload()));

        assertThat(e.getMessage(), equalTo("Unknown payload type: PAYLOAD_NOT_SET"));
    }

    private HubEventProto getTestHubEventProto() {
        return HubEventProto.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP_PROTO)
                .setScenarioAdded(ScenarioAddedEventProto.newBuilder()
                        .setName(SCENARIO_NAME)
                        .addAllCondition(getTestConditionsProto())
                        .addAllAction(getTestActionsProto())
                        .build())
                .build();
    }

    private List<ScenarioConditionProto> getTestConditionsProto() {
        return getPossibleConditionTypes().stream()
                .flatMap(conditionType -> getPossibleOperations().stream()
                        .map(operation -> getTestScenarioConditionProto(conditionType, operation,
                                builder -> builder.setIntValue(INT_VALUE))))
                .toList();
    }

    private List<DeviceActionProto> getTestActionsProto() {
        final List<DeviceActionProto> actions = new ArrayList<>();
        for (int i = 0; i < actionTypesProto.size(); i++) {
            DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                    .setSensorId(SENSOR_ID)
                    .setType(actionTypesProto.get(i));
            if (i % 2 == 0) {
                builder.setValue(INT_VALUE);
            }
            actions.add(builder.build());
        }
        return actions;
    }

    private HubEventAvro getTestHubEventAvro() {
        return HubEventAvro.newBuilder()
                .setHubId(HUB_ID)
                .setTimestamp(TIMESTAMP)
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(SCENARIO_NAME)
                        .setConditions(getTestConditionsAvro())
                        .setActions(getTestActionsAvro())
                        .build())
                .build();
    }

    private List<ScenarioConditionAvro> getTestConditionsAvro() {
        return getTestConditionTypesAvro().stream()
                .flatMap(conditionType -> getTestOperationsAvro().stream()
                        .map(operation -> getTestScenarioConditionAvro(conditionType, operation,
                                builder -> builder.setValue(INT_VALUE))))
                .toList();
    }

    private List<DeviceActionAvro> getTestActionsAvro() {
        final List<DeviceActionAvro> actions = new ArrayList<>();
        for (int i = 0; i < actionTypesAvro.size(); i++) {
            actions.add(DeviceActionAvro.newBuilder()
                    .setSensorId(SENSOR_ID)
                    .setType(actionTypesAvro.get(i))
                    .setValue(i % 2 == 0 ? INT_VALUE : null)
                    .build());
        }
        return actions;
    }
}