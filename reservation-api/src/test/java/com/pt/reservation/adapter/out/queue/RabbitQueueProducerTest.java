package com.pt.reservation.adapter.out.queue;

import com.pt.reservation.application.properties.QueueProperties;
import com.pt.reservation.domain.dto.queue.BookQueueMessage;
import com.pt.reservation.domain.dto.queue.ReservationQueueMessage;
import com.pt.reservation.domain.dto.queue.UserQueueMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RabbitQueueProducerTest {
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private QueueProperties queueProperties;

    @InjectMocks
    private RabbitQueueProducer rabbitQueueProducer;

    @Test
    public void sendShouldConvertAndSendQueueMessage() {
        String queue = "test-queue";
        QueueProperties.QueueProducerProperties producerProps = new QueueProperties.QueueProducerProperties();
        producerProps.setEmailNotificationReservationQueue(queue);

        when(queueProperties.getProducer()).thenReturn(producerProps);

        ReservationQueueMessage reservationQueueMessage = new ReservationQueueMessage(
                new UserQueueMessage(1L, "User Test", "test@email.com"),
                Collections.singletonList(new BookQueueMessage("Title", "Author")), "PENDING");

        rabbitQueueProducer.send(reservationQueueMessage);

        verify(rabbitTemplate, times(1)).convertAndSend("", queue, reservationQueueMessage);
    }
}
