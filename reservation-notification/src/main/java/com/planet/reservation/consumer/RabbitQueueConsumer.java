package com.planet.reservation.consumer;

import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;
import com.planet.reservation.properties.QueueProperties;
import com.planet.reservation.service.ReservationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitQueueConsumer {
    private final QueueProperties queueProperties;
    private final ReservationService reservationService;

    private static final Logger log = LogManager.getLogger(RabbitQueueConsumer.class);

    public RabbitQueueConsumer(QueueProperties queueProperties, ReservationService reservationService) {
        this.queueProperties = queueProperties;
        this.reservationService = reservationService;
    }

    @RabbitListener(queues = "#{queueProperties.consumer.emailNotificationReservationQueue}")
    public void consume(ReservationQueueMessage reservationQueueMessage) {
        log.info("Received reservation notification from queue {}", queueProperties.getConsumer().getEmailNotificationReservationQueue());
        reservationService.process(reservationQueueMessage);
    }
}
