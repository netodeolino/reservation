package com.pt.reservation.application.ports.out.queue;

import com.pt.reservation.domain.dto.queue.ReservationQueueMessage;

public interface ReservationQueuePort {
    void send(ReservationQueueMessage reservationQueueMessage);
}
