package com.planet.reservation.application.ports.in;

import com.planet.reservation.domain.dto.request.BookRequest;
import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.entities.BookEntity;

public interface ReservationItemUseCase {
    void validateNumberOfBooks(ReservationRequest reservationRequest);
    void validateBookAvailability(BookEntity bookEntity, BookRequest bookRequest);
}
