package com.example.central.service;

import com.example.central.model.SensorData;
import com.example.central.model.SensorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock
    private ThresholdService thresholdService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MonitoringService monitoringService;

    @Test
    void testProcessSensorData_Success() throws Exception {
        String validJsonMessage = """
            {
                "sensorType":"TEMPERATURE",
                "sensor_id":"t1",
                "value":45,
                "timestamp":1734294926002
            }
            """;

        SensorData expectedSensorData = new SensorData(SensorType.TEMPERATURE, "t1", 45, 1734294926002L);

        when(objectMapper.readValue(validJsonMessage, SensorData.class)).thenReturn(expectedSensorData);
        when(thresholdService.checkThreshold(expectedSensorData)).thenReturn(Mono.just("Temperature threshold exceeded"));

        monitoringService.processSensorData(validJsonMessage);

        verify(thresholdService, times(1)).checkThreshold(expectedSensorData);
    }

    @Test
    void testProcessSensorData_InvalidJson() throws Exception {
        String invalidJsonMessage = "{invalidJson}";

        when(objectMapper.readValue(invalidJsonMessage, SensorData.class)).thenThrow(new RuntimeException("Invalid JSON"));

        monitoringService.processSensorData(invalidJsonMessage);

        verify(thresholdService, never()).checkThreshold(any());
    }

    @Test
    void testProcessSensorData_ThresholdCheckReturnsEmpty() throws Exception {
        // Arrange
        String validJsonMessage = """
            {
                "sensorType":"HUMIDITY",
                "sensor_id":"h1",
                "value":40,
                "timestamp":1734294926002
            }
            """;

        SensorData expectedSensorData = new SensorData(SensorType.HUMIDITY, "h1", 40, 1734294926002L);

        // Mock ObjectMapper to return the parsed SensorData
        when(objectMapper.readValue(validJsonMessage, SensorData.class)).thenReturn(expectedSensorData);

        // Mock ThresholdService to return an empty Mono
        when(thresholdService.checkThreshold(expectedSensorData)).thenReturn(Mono.empty());

        // Act
        monitoringService.processSensorData(validJsonMessage);

        // Assert & Verify
        verify(thresholdService, times(1)).checkThreshold(expectedSensorData);
        verifyNoMoreInteractions(thresholdService);
    }
}
