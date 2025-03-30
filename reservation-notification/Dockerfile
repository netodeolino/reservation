FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/*.jar reservation-notification.jar

ENTRYPOINT ["java", "-jar", "reservation-notification.jar"]
