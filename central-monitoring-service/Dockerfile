## Stage 1: Build the application
#FROM maven:3.8.8-eclipse-temurin-17 AS build
#
## Set the working directory
#WORKDIR /app
#
## Copy the Maven configuration file
#COPY pom.xml .
#
## Download dependencies
#RUN mvn dependency:go-offline
#
## Copy the source code
#COPY src ./src
#
## Build the application
#RUN mvn clean package -DskipTests
#
## Optional: Run the application to validate during the build
##RUN java -jar target/central-monitoring-service.jar
##RUN sleep 100 && curl --fail http://localhost:8082/actuator/health || exit 1
#
## Stage 2: Create the runtime image
#FROM openjdk:17-jdk-slim
#
## Set the working directory
#WORKDIR /app
#
## Copy the built JAR from the build stage
#COPY --from=build /app/target/central-monitoring-service-0.0.1-SNAPSHOT.jar central-monitoring-service.jar
#
## Expose the application port
#EXPOSE 8082
#
## Define the entry point
#ENTRYPOINT ["java", "-jar", "central-monitoring-service.jar"]

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/central-monitoring-service.jar .
ENTRYPOINT ["java", "-jar", "central-monitoring-service.jar"]
