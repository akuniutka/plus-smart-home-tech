package ru.yandex.practicum.kafka.telemetry.client;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class KafkaListenerProperties {

    private List<String> topics;
    private Map<String, String> properties;
    private Duration pollTimeout = Duration.ofMillis(10_000L);
    private KafkaListener.Strategy strategy = KafkaListener.Strategy.AT_LEAST_ONCE;
    private int commitBatchSize;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(final List<String> topics) {
        this.topics = topics;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }

    public Duration getPollTimeout() {
        return pollTimeout;
    }

    public void setPollTimeout(final long pollTimeout) {
        this.pollTimeout = Duration.ofMillis(pollTimeout);
    }

    public KafkaListener.Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(final KafkaListener.Strategy strategy) {
        this.strategy = strategy;
    }

    public int getCommitBatchSize() {
        return commitBatchSize;
    }

    public void setCommitBatchSize(final int commitBatchSize) {
        this.commitBatchSize = commitBatchSize;
    }
}
