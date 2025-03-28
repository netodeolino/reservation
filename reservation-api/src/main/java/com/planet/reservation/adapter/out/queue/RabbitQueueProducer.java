package com.planet.reservation.adapter.out.queue;

import com.planet.reservation.application.ports.out.queue.ReservationQueuePort;
import com.planet.reservation.application.properties.QueueProperties;
import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitQueueProducer implements ReservationQueuePort {
    private final RabbitTemplate rabbitTemplate;
    private final QueueProperties queueProperties;

    public RabbitQueueProducer(RabbitTemplate rabbitTemplate, QueueProperties queueProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueProperties = queueProperties;
    }

    @Override
    public void send(ReservationQueueMessage reservationQueueMessage) {
        String queue = queueProperties.getProducer().getEmailNotificationReservationQueue();
        rabbitTemplate.convertAndSend("", queue, reservationQueueMessage);
    }
}
