FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar reservation-api.jar

ENTRYPOINT ["java", "-jar", "reservation-api.jar"]
