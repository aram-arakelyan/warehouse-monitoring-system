package com.example.warehouse.listener;

import com.example.warehouse.service.SensorUDPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class listens for the application startup event and triggers the SensorUDPService
 * to start processing messages.
 */
@Component
@Slf4j
public class AppStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final SensorUDPService sensorService;

    @Autowired
    public AppStartupListener(SensorUDPService sensorService) {
        this.sensorService = sensorService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application started. Starting SensorUDPService...");
        sensorService.startProcessingMessages();
    }
}
