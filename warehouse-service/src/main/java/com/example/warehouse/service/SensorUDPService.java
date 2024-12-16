package com.example.warehouse.service;

import com.example.warehouse.model.SensorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SensorUDPService implements SensorService {

    private final RabbitTemplate rabbitTemplate;
    private final UdpListener udpListener;
    private final String exchange;
    private final String routingKey;
    private final ObjectMapper objectMapper;

    /**
     * Constructor to inject dependencies required for the SensorUDPService.
     *
     * @param rabbitTemplate The RabbitTemplate used for sending messages to RabbitMQ.
     * @param udpListener    The UDP listener responsible for receiving incoming UDP messages.
     * @param exchange       The name of the RabbitMQ exchange.
     * @param routingKey     The routing key used for sending messages to RabbitMQ.
     * @param objectMapper   The ObjectMapper used for serializing messages to JSON.
     */
    public SensorUDPService(
            RabbitTemplate rabbitTemplate,
            UdpListener udpListener,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-key}") String routingKey,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.udpListener = udpListener;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.objectMapper = objectMapper;
    }

    /**
     * Starts processing messages from the UDP listener and forwards them to RabbitMQ.
     * The messages are converted to JSON format before being sent to RabbitMQ.
     */
    @Override
    public void startProcessingMessages() {
        udpListener.startListening()
                .subscribe(
                        message -> {
                            log.info("[SensorService] Received UDP message: {}", message);
                            sendMessageToRabbitMQ(message);
                        },
                        error -> log.error("[SensorService] Error processing UDP message", error),
                        () -> log.info("[SensorService] UDP message processing completed.")
                );
    }

    /**
     * Sends a SensorData message to RabbitMQ.
     *
     * @param message The SensorData object containing sensor information to be sent to RabbitMQ.
     *                It includes details like sensor type, sensor ID, and value.
     */
    private void sendMessageToRabbitMQ(SensorData message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            log.info("[SensorService] Publishing to RabbitMQ: Exchange={}, RoutingKey={}, Message={}",
                    exchange, routingKey, jsonMessage);

            rabbitTemplate.convertAndSend(exchange, routingKey, jsonMessage);

            log.info("[SensorService] Successfully published to RabbitMQ: {}", jsonMessage);
        } catch (Exception ex) {
            log.error("[SensorService] Error publishing to RabbitMQ: {}", ex.getMessage(), ex);
        }
    }
}
