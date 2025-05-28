package com.pt.reservation.service.notification;

import com.pt.reservation.dto.notification.EmailMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender<EmailMessage> {
    private static final Logger log = LogManager.getLogger(EmailNotificationSender.class);

    @Override
    public void send(EmailMessage message) {
        // Simulating email notification!
        log.info("Sending email to {}", message.to());
    }
}
