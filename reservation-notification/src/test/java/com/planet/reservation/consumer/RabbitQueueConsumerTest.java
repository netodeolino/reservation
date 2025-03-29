package com.planet.reservation.consumer;

import com.planet.reservation.dto.queue.BookQueueMessage;
import com.planet.reservation.dto.queue.ReservationQueueMessage;
import com.planet.reservation.dto.queue.UserQueueMessage;
import com.planet.reservation.properties.QueueProperties;
import com.planet.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RabbitQueueConsumerTest {
    @Mock
    private QueueProperties queueProperties;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private RabbitQueueConsumer rabbitQueueConsumer;

    @Test
    public void consumeShouldReceiveConsumeMessageAndProcess() {
        QueueProperties.QueueConsumerProperties queueConsumerProperties = new QueueProperties.QueueConsumerProperties();
        queueConsumerProperties.setEmailNotificationReservationQueue("test-queue");

        when(queueProperties.getConsumer()).thenReturn(queueConsumerProperties);

        UserQueueMessage userQueueMessage = new UserQueueMessage(1L, "User test", "email@email.com");
        BookQueueMessage bookQueueMessage = new BookQueueMessage("Test title", "Test author");
        ReservationQueueMessage reservationQueueMessage = new ReservationQueueMessage(userQueueMessage, Collections.singletonList(bookQueueMessage), "PENDING");

        rabbitQueueConsumer.consume(reservationQueueMessage);

        verify(reservationService, times(1)).process(reservationQueueMessage);
    }

}
