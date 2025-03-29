package com.planet.reservation.service;

import com.planet.reservation.dto.notification.EmailMessage;
import com.planet.reservation.dto.notification.SMSMessage;
import com.planet.reservation.dto.queue.BookQueueMessage;
import com.planet.reservation.dto.queue.ReservationQueueMessage;
import com.planet.reservation.dto.queue.UserQueueMessage;
import com.planet.reservation.service.notification.EmailNotificationSender;
import com.planet.reservation.service.notification.NotificationSender;
import com.planet.reservation.service.notification.SMSNotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {
    private final NotificationSender<EmailMessage> emailNotificationSender = mock(EmailNotificationSender.class);
    private final NotificationSender<SMSMessage> smsNotificationSender = mock(SMSNotificationSender.class);
    private final ReservationServiceImpl reservationService = new ReservationServiceImpl(emailNotificationSender, smsNotificationSender);

    private ReservationQueueMessage reservationQueueMessage;

    private final String userName = "User test";
    private final String userEmail = "test@email.com";
    private final String planetEmail = "noreply@planet.com";

    @BeforeEach
    void setUp() {
        UserQueueMessage userQueueMessage = new UserQueueMessage(1L, userName, userEmail);
        BookQueueMessage bookQueueMessage = new BookQueueMessage("Test title", "Test author");

        reservationQueueMessage = new ReservationQueueMessage(userQueueMessage, Collections.singletonList(bookQueueMessage), "PENDING");
    }

    @Test
    void processShouldSendEmailAndSMSNotifications() {
        reservationService.process(reservationQueueMessage);

        verify(emailNotificationSender, times(1)).send(argThat(email ->
                email.from().equals(planetEmail) &&
                        email.to().equals(userEmail) &&
                        email.message().contains(String.format("Hello %s, your reservation is with status PENDING", userName))
        ));

        verify(smsNotificationSender, times(1)).send(argThat(sms ->
                sms.from().equals(planetEmail) &&
                        sms.to().equals(userEmail) &&
                        sms.message().contains(String.format("Hello %s, your reservation is with status PENDING", userName))
        ));
    }
}
