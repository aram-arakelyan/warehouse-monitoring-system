package com.example.warehouse.util;

import com.example.warehouse.model.SensorData;
import com.example.warehouse.model.SensorType;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class UdpMessageParser {

    /**
     * Parses a raw UDP message into a SensorData object.
     *
     * @param message    the raw UDP message
     * @param sensorType the type of sensor (e.g., TEMPERATURE, HUMIDITY)
     * @return a SensorData object
     */
    public static SensorData parseUdpMessageToSensorData(String message, SensorType sensorType) {
        Map<String, String> payload = new HashMap<>();
        String[] parts = message.split(";");

        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length == 2) {
                payload.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        String sensorId = payload.getOrDefault("sensor_id", "unknown");
        int value = Integer.parseInt(payload.getOrDefault("value", "0"));
        long timestamp = System.currentTimeMillis();

        return new SensorData(sensorType, sensorId, value, timestamp);
    }
}
