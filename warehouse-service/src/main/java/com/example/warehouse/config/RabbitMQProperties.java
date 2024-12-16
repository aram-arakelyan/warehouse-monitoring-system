package com.example.warehouse.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


@Getter
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private final String exchange;
    private final String queue;
    private final String routingKey;

    @ConstructorBinding
    public RabbitMQProperties(String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }
}
