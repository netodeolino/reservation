package com.planet.reservation.service;

import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger log = LogManager.getLogger(ReservationServiceImpl.class);

    @Override
    public void process(ReservationQueueMessage reservationQueueMessage) {
        log.info("Sending email notification to user id {}", reservationQueueMessage.user().id());
        // Simulating an email notification!
        log.info("Hello {}, your reservation is with status {}. Please pick up all books reserved. Books {}",
                reservationQueueMessage.user().name(), reservationQueueMessage.status(), reservationQueueMessage.books());
    }
}
