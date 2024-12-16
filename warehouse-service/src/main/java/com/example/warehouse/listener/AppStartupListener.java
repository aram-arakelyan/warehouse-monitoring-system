package com.example.warehouse.listener;

import com.example.warehouse.service.SensorUDPService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class listens for the application startup event and triggers the SensorUDPService
 * to start processing messages.
 */
@Component
@Slf4j
public class AppStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final SensorUDPService sensorService;

    public AppStartupListener(SensorUDPService sensorService) {
        this.sensorService = sensorService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application started. Starting SensorUDPService...");
        sensorService.startProcessingMessages();
    }
}
