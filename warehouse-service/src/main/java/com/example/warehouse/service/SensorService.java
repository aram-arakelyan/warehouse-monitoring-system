package com.example.warehouse.service;

/**
 * Interface defining the contract for Sensor Service implementations.
 */
public interface SensorService {

    /**
     * Start processing messages from the UDP listener and publish them to RabbitMQ.
     */
    void startProcessingMessages();
}
