package com.example.warehouse.util;

import com.example.warehouse.model.SensorData;
import com.example.warehouse.model.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UdpMessageParserTest {

    @Test
    void testParseUdpMessageToSensorData_ValidMessage() {
        String message = "sensor_id=S123;value=45";
        SensorType sensorType = SensorType.TEMPERATURE;

        SensorData result = UdpMessageParser.parseUdpMessageToSensorData(message, sensorType);

        assertNotNull(result, "SensorData should not be null");
        assertEquals("S123", result.sensorId(), "Sensor ID should be correctly parsed");
        assertEquals(45, result.value(), "Value should be correctly parsed");
        assertEquals(sensorType, result.sensorType(), "Sensor type should match the input sensor type");
    }

    @Test
    void testParseUdpMessageToSensorData_MessageWithExtraWhitespace() {
        String message = " sensor_id = S123 ; value = 45 ";
        SensorType sensorType = SensorType.HUMIDITY;

        SensorData result = UdpMessageParser.parseUdpMessageToSensorData(message, sensorType);

        assertNotNull(result, "SensorData should not be null");
        assertEquals("S123", result.sensorId(), "Sensor ID should be correctly parsed");
        assertEquals(45, result.value(), "Value should be correctly parsed");
        assertEquals(sensorType, result.sensorType(), "Sensor type should match the input sensor type");
    }

    @Test
    void testParseUdpMessageToSensorData_MessageWithMissingSensorId() {
        String message = "value=100";
        SensorType sensorType = SensorType.TEMPERATURE;

        SensorData result = UdpMessageParser.parseUdpMessageToSensorData(message, sensorType);

        assertNotNull(result, "SensorData should not be null");
        assertEquals("unknown", result.sensorId(), "Default sensor ID should be 'unknown'");
        assertEquals(100, result.value(), "Value should be correctly parsed");
        assertEquals(sensorType, result.sensorType(), "Sensor type should match the input sensor type");
    }

    @Test
    void testParseUdpMessageToSensorData_MessageWithInvalidValue() {
        String message = "sensor_id=S123;value=abc";
        SensorType sensorType = SensorType.HUMIDITY;

        NumberFormatException exception = assertThrows(NumberFormatException.class,
                () -> UdpMessageParser.parseUdpMessageToSensorData(message, sensorType),
                "Expected NumberFormatException to be thrown"
        );

        assertTrue(exception.getMessage().contains("Invalid integer value"), "Exception message should indicate invalid integer value");
    }

    @Test
    void testParseUdpMessageToSensorData_NullMessage() {
        String message = null;
        SensorType sensorType = SensorType.TEMPERATURE;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UdpMessageParser.parseUdpMessageToSensorData(message, sensorType),
                "Expected IllegalArgumentException to be thrown"
        );

        assertEquals("Message cannot be null or empty", exception.getMessage(), "Exception message should match expected message");
    }

    @Test
    void testParseUdpMessageToSensorData_EmptyMessage() {
        String message = " ";
        SensorType sensorType = SensorType.TEMPERATURE;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UdpMessageParser.parseUdpMessageToSensorData(message, sensorType),
                "Expected IllegalArgumentException to be thrown"
        );

        assertEquals("Message cannot be null or empty", exception.getMessage(), "Exception message should match expected message");
    }

    @Test
    void testParseUdpMessageToSensorData_MessageWithOnlySensorId() {
        String message = "sensor_id=S123";
        SensorType sensorType = SensorType.TEMPERATURE;

        SensorData result = UdpMessageParser.parseUdpMessageToSensorData(message, sensorType);

        assertNotNull(result, "SensorData should not be null");
        assertEquals("S123", result.sensorId(), "Sensor ID should be correctly parsed");
        assertEquals(0, result.value(), "Default value should be 0 when value is not provided");
        assertEquals(sensorType, result.sensorType(), "Sensor type should match the input sensor type");
    }
}
