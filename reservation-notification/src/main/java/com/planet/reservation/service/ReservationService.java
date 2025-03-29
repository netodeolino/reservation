package com.planet.reservation.service;

import com.planet.reservation.dto.queue.ReservationQueueMessage;

public interface ReservationService {
    void process(ReservationQueueMessage reservationQueueMessage);
}
