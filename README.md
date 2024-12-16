# Warehouse Monitoring System

## Overview

The Warehouse Monitoring System is a multi-module, reactive application designed to monitor and process sensor data in
real-time. It leverages Spring Boot WebFlux and Project Reactor to provide a non-blocking, event-driven architecture.

The system consists of two main services:

Warehouse Service:
Receives raw sensor data via UDP.
Parses the data and publishes structured messages to RabbitMQ.
Utilizes Spring WebFlux and Reactor to handle concurrent, asynchronous processing efficiently.

Central Monitoring Service:
Subscribes to the RabbitMQ queue.
Processes, stores, and monitors the incoming sensor data.
The system is built using Spring Boot, containerized with Docker, and orchestrated using Docker Compose. It ensures
reliable, real-time data flow from sensors to centralized monitoring using RabbitMQ as the message broker.

## Technologies

- Java 17
- Spring Boot 3.x
- Docker and Docker Compose
- RabbitMQ
- Maven

## Installation

1. Install Java 17 and Maven.
2. Install Docker and Docker Compose.

## Running the Project

1. Build the project:
    ```bash
    cd central-monitoring-service 
    mvn clean install
    cd ../warehouse-service
    mvn clean install
    ```
2. Start the services:
    ```bash
    docker-compose up --build
    ```
3. Sending Messages via Netcat
   You can simulate UDP sensor messages using the netcat command-line tool.
   Please, make sure you have netcat locally installed for the emulation.

  ```bash
  apt update && apt install -y netcat
  ```

Temperature Sensor Examples (UDP Port: 3344):

  ```bash
    echo -n "sensor_id=t1;value=44" | nc -u localhost 3344
    echo -n "sensor_id=t2;value=34" | nc -u localhost 3344
  ```

Humidity Sensor Examples (UDP Port: 3355):

  ```bash
    echo -n "sensor_id=h1;value=45" | nc -u localhost 3355
    echo -n "sensor_id=h2;value=51" | nc -u localhost 3355
  ```
