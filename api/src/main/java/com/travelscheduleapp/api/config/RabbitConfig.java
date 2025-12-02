package com.travelscheduleapp.api.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String TRIP_QUEUE = "trip-generation-queue";

    @Bean
    public Queue tripQueue() {
        return new Queue(TRIP_QUEUE, true);
    }
}
