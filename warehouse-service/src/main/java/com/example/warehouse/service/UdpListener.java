package com.example.warehouse.service;

import com.example.warehouse.model.SensorData;
import com.example.warehouse.model.SensorType;
import com.example.warehouse.util.UdpMessageParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class UdpListener {

    private static final int BUFFER_SIZE = 1024;

    private final int temperaturePort;
    private final int humidityPort;

    public UdpListener(
            @Value("${udp.temperature-port}") int temperaturePort,
            @Value("${udp.humidity-port}") int humidityPort
    ) {
        this.temperaturePort = temperaturePort;
        this.humidityPort = humidityPort;
    }

    /**
     * Starts listening on UDP ports for incoming sensor data.
     *
     * @return a Flux stream of parsed SensorData objects from UDP packets
     */
    public Flux<SensorData> startListening() {
        return Flux.merge(
                listenOnPort(temperaturePort, SensorType.TEMPERATURE).subscribeOn(Schedulers.boundedElastic()),
                listenOnPort(humidityPort, SensorType.HUMIDITY).subscribeOn(Schedulers.boundedElastic())
        );
    }

    /**
     * Listens for incoming UDP messages on the specified port and parses them into SensorData objects.
     *
     * @param port       the UDP port to listen on
     * @param sensorType the type of sensor (e.g., TEMPERATURE, HUMIDITY)
     * @return a Flux stream of parsed SensorData objects
     */
    private Flux<SensorData> listenOnPort(int port, SensorType sensorType) {
        return Flux.<SensorData>create(sink -> {
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket(port);
                        log.info("[UDP Listener] Listening for {} messages on port {}", sensorType, port);

                        byte[] buffer = new byte[BUFFER_SIZE];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        while (!sink.isCancelled()) {
                            try {
                                socket.receive(packet);
                                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                                SensorData sensorData = UdpMessageParser.parseUdpMessageToSensorData(message, sensorType);
                                sink.next(sensorData);
                            } catch (Exception e) {
                                log.error("[UDP Listener] Error receiving packet on port {}: {}", port, e.getMessage());
                            }
                            try {
                                Thread.sleep(10); // Small delay to prevent busy-waiting
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                log.error("[UDP Listener] Thread interrupted while sleeping", e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("[UDP Listener] Error binding to port {}: {}", port, e.getMessage(), e);
                        sink.error(e);
                    } finally {
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                            log.info("[UDP Listener] Closed socket on port {}", port);
                        }
                    }
                })
                .doOnCancel(() -> log.info("[UDP Listener] Stopped listening on port {}", port));
    }
}
