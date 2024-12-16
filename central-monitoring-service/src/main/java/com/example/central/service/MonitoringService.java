package com.example.central.service;

import com.example.central.model.SensorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MonitoringService {

    private final ThresholdService thresholdService;
    private final ObjectMapper objectMapper;

    public MonitoringService(ThresholdService thresholdService, ObjectMapper objectMapper) {
        this.thresholdService = thresholdService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queue}") // Queue name externalized to application.yml
    public void processSensorData(String message) {
        log.info("[Central Monitoring Service] Received raw message: {}", message);

        try {
            // Deserialize the JSON string into SensorData object
            SensorData sensorData = objectMapper.readValue(message, SensorData.class);

            log.info("[Central Monitoring Service] Parsed Sensor Data: {}", sensorData);

            // Process the thresholds using ThresholdService
            Mono.just(sensorData)
                    .flatMap(thresholdService::checkThreshold)
                    .subscribe(
                            result -> {
                                if (result != null) {
                                    log.error("[ALARM] {}", result); // Stronger log level for alarms
                                }
                            },
                            error -> log.error("[Central Monitoring Service] Error processing sensor data: {}", error.getMessage(), error),
                            () -> log.info("[Central Monitoring Service] Processing completed for sensor data: {}", sensorData)
                    );
        } catch (Exception ex) {
            log.error("[Central Monitoring Service] Failed to parse or process message: {}", ex.getMessage(), ex);
        }
    }
}
