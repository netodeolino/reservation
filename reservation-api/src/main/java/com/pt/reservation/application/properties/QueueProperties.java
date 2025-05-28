package com.pt.reservation.application.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.queue")
public class QueueProperties {
    private QueueProducerProperties producer;

    public QueueProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(QueueProducerProperties producer) {
        this.producer = producer;
    }

    public static class QueueProducerProperties {
        private String emailNotificationReservationQueue;

        public String getEmailNotificationReservationQueue() {
            return emailNotificationReservationQueue;
        }

        public void setEmailNotificationReservationQueue(String emailNotificationReservationQueue) {
            this.emailNotificationReservationQueue = emailNotificationReservationQueue;
        }
    }
}
