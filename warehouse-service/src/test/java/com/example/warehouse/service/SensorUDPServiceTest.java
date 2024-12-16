package com.example.warehouse.service;

import com.example.warehouse.model.SensorData;
import com.example.warehouse.model.SensorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorUDPServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UdpListener udpListener;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SensorUDPService sensorUDPService;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private final String exchange = "test-exchange";
    private final String routingKey = "test-routing-key";

    @BeforeEach
    void setUp() {
        // Re-initialize the service with mocked dependencies
        sensorUDPService = new SensorUDPService(rabbitTemplate, udpListener, exchange, routingKey, objectMapper);
    }

    @Test
    void startProcessingMessages_shouldSendMessageToRabbitMQ_whenUdpListenerReceivesMessage() throws Exception {
        SensorData sensorData = new SensorData(SensorType.TEMPERATURE, "sensor-1", 40, System.currentTimeMillis());
        String jsonMessage = "{\"sensorType\":\"TEMPERATURE\",\"sensorId\":\"sensor-1\",\"value\":40,\"timestamp\":123456789}";

        when(udpListener.startListening()).thenReturn(Flux.just(sensorData));
        when(objectMapper.writeValueAsString(sensorData)).thenReturn(jsonMessage);

        sensorUDPService.startProcessingMessages();

        verify(rabbitTemplate, timeout(1000)).convertAndSend(eq(exchange), eq(routingKey), messageCaptor.capture());
        assertEquals(jsonMessage, messageCaptor.getValue());
        verify(objectMapper).writeValueAsString(sensorData);
        verify(udpListener).startListening();
    }

    @Test
    void startProcessingMessages_shouldLogError_whenObjectMapperThrowsException() throws Exception {
        SensorData sensorData = new SensorData(SensorType.HUMIDITY, "sensor-2", 70, System.currentTimeMillis());
        when(udpListener.startListening()).thenReturn(Flux.just(sensorData));
        when(objectMapper.writeValueAsString(sensorData)).thenThrow(new RuntimeException("JSON serialization failed"));

        sensorUDPService.startProcessingMessages();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(objectMapper).writeValueAsString(sensorData);
        verify(udpListener).startListening();
    }

    @Test
    void startProcessingMessages_shouldHandleMultipleMessagesCorrectly() throws Exception {
        SensorData sensorData1 = new SensorData(SensorType.TEMPERATURE, "sensor-1", 40, System.currentTimeMillis());
        SensorData sensorData2 = new SensorData(SensorType.HUMIDITY, "sensor-2", 55, System.currentTimeMillis());

        String jsonMessage1 = "{\"sensorType\":\"TEMPERATURE\",\"sensorId\":\"sensor-1\",\"value\":40,\"timestamp\":123456789}";
        String jsonMessage2 = "{\"sensorType\":\"HUMIDITY\",\"sensorId\":\"sensor-2\",\"value\":55,\"timestamp\":123456789}";

        when(udpListener.startListening()).thenReturn(Flux.just(sensorData1, sensorData2));
        when(objectMapper.writeValueAsString(sensorData1)).thenReturn(jsonMessage1);
        when(objectMapper.writeValueAsString(sensorData2)).thenReturn(jsonMessage2);

        sensorUDPService.startProcessingMessages();

        verify(rabbitTemplate, timeout(1000).times(2)).convertAndSend(eq(exchange), eq(routingKey), messageCaptor.capture());
        assertTrue(messageCaptor.getAllValues().contains(jsonMessage1));
        assertTrue(messageCaptor.getAllValues().contains(jsonMessage2));
        verify(objectMapper).writeValueAsString(sensorData1);
        verify(objectMapper).writeValueAsString(sensorData2);
        verify(udpListener).startListening();
    }

    @Test
    void startProcessingMessages_shouldNotSendMessage_whenUdpListenerCompletesWithoutData() {
        when(udpListener.startListening()).thenReturn(Flux.empty());

        sensorUDPService.startProcessingMessages();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(udpListener).startListening();
    }

    @Test
    void startProcessingMessages_shouldNotSendMessage_whenUdpListenerThrowsError() {
        when(udpListener.startListening()).thenReturn(Flux.error(new RuntimeException("UDP listener error")));

        sensorUDPService.startProcessingMessages();

        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
        verify(udpListener).startListening();
    }
}
