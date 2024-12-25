package ru.yandex.practicum.telemetry.aggregator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.yandex.practicum.kafka.telemetry.client.KafkaListenerProperties;

@ConfigurationProperties("kafka.event-listener")
public class EventListenerProperties extends KafkaListenerProperties {

}
