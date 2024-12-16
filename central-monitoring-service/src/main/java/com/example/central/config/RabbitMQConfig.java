package com.example.central.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "sensor.data.queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }
}
