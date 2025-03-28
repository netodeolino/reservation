package com.planet.reservation.adapter.out.queue;

import com.planet.reservation.application.ports.out.queue.ReservationQueuePort;
import com.planet.reservation.application.properties.QueueProperties;
import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitQueueProducer implements ReservationQueuePort {
    private final RabbitTemplate rabbitTemplate;
    private final QueueProperties queueProperties;

    private static final Logger log = LogManager.getLogger(RabbitQueueProducer.class);

    public RabbitQueueProducer(RabbitTemplate rabbitTemplate, QueueProperties queueProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueProperties = queueProperties;
    }

    @Override
    public void send(ReservationQueueMessage reservationQueueMessage) {
        String queue = queueProperties.getProducer().getEmailNotificationReservationQueue();
        log.info("Sending the reservation message of the user id {} to the queue {}", reservationQueueMessage.user().id(), queue);
        rabbitTemplate.convertAndSend("", queue, reservationQueueMessage);
        log.info("Sent the reservation message of the user id {}", reservationQueueMessage.user().id());
    }
}
