package com.planet.reservation.application.ports.out.queue;

import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;

public interface ReservationQueuePort {
    void send(ReservationQueueMessage reservationQueueMessage);
}
