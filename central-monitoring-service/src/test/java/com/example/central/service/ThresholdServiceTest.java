package com.example.central.service;

import com.example.central.model.SensorData;
import com.example.central.model.SensorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

class ThresholdServiceTest {

    private ThresholdService thresholdService;

    private final double temperatureThreshold = 35.0;
    private final double humidityThreshold = 50.0;

    private DecimalFormat decimalFormat;

    @BeforeAll
    static void setGlobalLocale() {
        Locale.setDefault(Locale.US);
    }

    @BeforeEach
    void setUp() {
        thresholdService = new ThresholdService(temperatureThreshold, humidityThreshold);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat = new DecimalFormat("0.00", symbols);
    }

    @Test
    void testCheckThreshold_TemperatureExceeded() {
        SensorData sensorData = new SensorData(SensorType.TEMPERATURE, "t1", 40, System.currentTimeMillis());

        String expectedMessage = String.format(
                "Temperature threshold exceeded: 40°C (Threshold: %s°C)",
                decimalFormat.format(temperatureThreshold)
        );

        Mono<String> result = thresholdService.checkThreshold(sensorData);

        StepVerifier.create(result)
                .expectNext(expectedMessage)
                .verifyComplete();
    }

    @Test
    void testCheckThreshold_HumidityExceeded() {
        SensorData sensorData = new SensorData(SensorType.HUMIDITY, "h1", 55, System.currentTimeMillis());

        String expectedMessage = String.format(
                "Humidity threshold exceeded: 55%% (Threshold: %s%%)",
                decimalFormat.format(humidityThreshold)
        );

        Mono<String> result = thresholdService.checkThreshold(sensorData);

        StepVerifier.create(result)
                .expectNext(expectedMessage)
                .verifyComplete();
    }

    @Test
    void testCheckThreshold_TemperatureNotExceeded() {
        SensorData sensorData = new SensorData(SensorType.TEMPERATURE, "t1", 34, System.currentTimeMillis());

        Mono<String> result = thresholdService.checkThreshold(sensorData);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testCheckThreshold_HumidityNotExceeded() {
        SensorData sensorData = new SensorData(SensorType.HUMIDITY, "h1", 45, System.currentTimeMillis());

        Mono<String> result = thresholdService.checkThreshold(sensorData);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testCheckThreshold_ThresholdExactlyMet() {
        SensorData tempData = new SensorData(SensorType.TEMPERATURE, "t1", 35, System.currentTimeMillis());
        SensorData humidityData = new SensorData(SensorType.HUMIDITY, "h1", 50, System.currentTimeMillis());

        StepVerifier.create(thresholdService.checkThreshold(tempData))
                .verifyComplete();

        StepVerifier.create(thresholdService.checkThreshold(humidityData))
                .verifyComplete();
    }
}
