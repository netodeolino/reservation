package com.planet.reservation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.scheduling")
public class SchedulingProperties {
    private NotPickedUpReservationFixed notPickedUpReservationFixed;

    public NotPickedUpReservationFixed getNotPickedUpReservationFixed() {
        return notPickedUpReservationFixed;
    }

    public void setNotPickedUpReservationFixed(NotPickedUpReservationFixed notPickedUpReservationFixed) {
        this.notPickedUpReservationFixed = notPickedUpReservationFixed;
    }

    public static class NotPickedUpReservationFixed {
        private String delayTime;
        private int expirationTime;

        public String getDelayTime() {
            return delayTime;
        }

        public void setDelayTime(String delayTime) {
            this.delayTime = delayTime;
        }

        public int getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(int expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}
