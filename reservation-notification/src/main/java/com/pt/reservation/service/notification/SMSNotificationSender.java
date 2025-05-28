package com.pt.reservation.service.notification;

import com.pt.reservation.dto.notification.SMSMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SMSNotificationSender implements NotificationSender<SMSMessage> {
    private static final Logger log = LogManager.getLogger(SMSNotificationSender.class);

    @Override
    public void send(SMSMessage message) {
        // Simulating SMS notification!
        log.info("Sending SMS to {}", message.to());
    }
}
