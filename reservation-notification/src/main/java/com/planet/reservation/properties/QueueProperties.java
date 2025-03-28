package com.planet.reservation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.queue")
public class QueueProperties {
    private QueueConsumerProperties consumer;

    public QueueConsumerProperties getConsumer() {
        return consumer;
    }

    public void setConsumer(QueueConsumerProperties consumer) {
        this.consumer = consumer;
    }

    public static class QueueConsumerProperties {
        private String emailNotificationReservationQueue;

        public String getEmailNotificationReservationQueue() {
            return emailNotificationReservationQueue;
        }

        public void setEmailNotificationReservationQueue(String emailNotificationReservationQueue) {
            this.emailNotificationReservationQueue = emailNotificationReservationQueue;
        }
    }
}
