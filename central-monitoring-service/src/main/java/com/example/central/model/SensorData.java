package com.example.central.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SensorData(SensorType sensorType, @JsonProperty("sensor_id") String sensorId, int value, long timestamp) {

}
