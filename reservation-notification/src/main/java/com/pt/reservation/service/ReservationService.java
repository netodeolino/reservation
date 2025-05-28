package com.pt.reservation.service;

import com.pt.reservation.dto.queue.ReservationQueueMessage;

public interface ReservationService {
    void process(ReservationQueueMessage reservationQueueMessage);
}
