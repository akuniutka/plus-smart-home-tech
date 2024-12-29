package ru.yandex.practicum.telemetry.analyzer.listener;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerProperties;

@ConfigurationProperties("kafka.hub-event-listener")
public class HubEventListenerProperties extends KafkaListenerProperties {

}
