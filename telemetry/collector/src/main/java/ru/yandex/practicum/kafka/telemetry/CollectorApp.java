package ru.yandex.practicum.kafka.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CollectorApp {

    public static void main(final String[] args) {
        SpringApplication.run(CollectorApp.class, args);
    }
}
