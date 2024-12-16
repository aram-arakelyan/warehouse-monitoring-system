package com.example.warehouse.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public RabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.rabbitMQProperties = rabbitMQProperties;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(rabbitMQProperties.getExchange());
    }

    @Bean
    public Queue queue() {
        return new Queue(rabbitMQProperties.getQueue());
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(rabbitMQProperties.getRoutingKey());
    }
}
