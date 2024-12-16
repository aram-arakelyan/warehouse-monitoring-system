## Stage 1: Build the application using Maven
#FROM maven:3.8.8-eclipse-temurin-17 AS build
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the pom.xml and download dependencies
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
## Copy the source code
#COPY src ./src
#
## Build the application
#RUN mvn clean package -DskipTests
#
## Run the application to validate during the build
##RUN java -jar target/warehouse-service.jar
##RUN sleep 100 && curl --fail http://localhost:8081/actuator/health || exit 1
#
## Stage 2: Run the application
#FROM openjdk:17-jdk-slim
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the built JAR file from the build stage
#COPY --from=build /app/target/warehouse-service-0.0.1-SNAPSHOT.jar warehouse-service.jar
#
## Expose the application port
#EXPOSE 8081
#
## Define the entry point for the container
#ENTRYPOINT ["java", "-jar", "warehouse-service.jar"]
#TODO apt update && apt install -y netcat
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/warehouse-service.jar .
ENTRYPOINT ["java", "-jar", "warehouse-service.jar"]


