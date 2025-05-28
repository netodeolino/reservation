package com.pt.reservation.service;

import com.pt.reservation.dto.notification.EmailMessage;
import com.pt.reservation.dto.notification.SMSMessage;
import com.pt.reservation.dto.queue.ReservationQueueMessage;
import com.pt.reservation.service.notification.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final NotificationSender<EmailMessage> emailNotificationSender;
    private final NotificationSender<SMSMessage> smsNotificationSender;

    private static final Logger log = LogManager.getLogger(ReservationServiceImpl.class);

    public ReservationServiceImpl(
            NotificationSender<EmailMessage> emailNotificationSender, NotificationSender<SMSMessage> smsNotificationSender
    ) {
        this.emailNotificationSender = emailNotificationSender;
        this.smsNotificationSender = smsNotificationSender;
    }

    @Override
    public void process(ReservationQueueMessage reservationQueueMessage) {
        log.info("Sending email notification to user id {}", reservationQueueMessage.user().id());

        String from = "noreply@planet.com";
        String to = reservationQueueMessage.user().email();
        String message = String.format("Hello %s, your reservation is with status %s. Please pick up all books reserved. Books %s",
                reservationQueueMessage.user().name(), reservationQueueMessage.status(), reservationQueueMessage.books());

        emailNotificationSender.send(new EmailMessage(from, to, message));
        smsNotificationSender.send(new SMSMessage(from, to, message));
    }
}
