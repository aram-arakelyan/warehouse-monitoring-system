package com.example.central.service;

import com.example.central.model.SensorData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ThresholdService {

    private final double temperatureThreshold;
    private final double humidityThreshold;

    public ThresholdService(
            @Value("${monitoring.thresholds.temperature}") double temperatureThreshold,
            @Value("${monitoring.thresholds.humidity}") double humidityThreshold
    ) {
        this.temperatureThreshold = temperatureThreshold;
        this.humidityThreshold = humidityThreshold;
    }

    /**
     * Checks if the SensorData exceeds the threshold.
     *
     * @param data SensorData object
     * @return Mono<String> with alarm message if threshold exceeded, or empty
     */
    public Mono<String> checkThreshold(SensorData data) {
        return switch (data.sensorType()) {
            case TEMPERATURE -> {
                if (data.value() > temperatureThreshold) {
                    String alarm = String.format(
                            "Temperature threshold exceeded: %d°C (Threshold: %.2f°C)",
                            data.value(), temperatureThreshold
                    );
                    yield Mono.just(alarm);
                }
                yield Mono.empty();
            }
            case HUMIDITY -> {
                if (data.value() > humidityThreshold) {
                    String alarm = String.format(
                            "Humidity threshold exceeded: %d%% (Threshold: %.2f%%)",
                            data.value(), humidityThreshold
                    );
                    yield Mono.just(alarm);
                }
                yield Mono.empty();
            }
        };
    }
}
