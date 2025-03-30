FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar reservation-batch.jar

ENTRYPOINT ["java", "-jar", "reservation-batch.jar"]
