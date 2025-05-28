package com.pt.reservation.application.ports.in;

import com.pt.reservation.domain.dto.request.BookRequest;
import com.pt.reservation.domain.dto.request.ReservationRequest;
import com.pt.reservation.domain.entities.BookEntity;

public interface ReservationItemUseCase {
    void validateNumberOfBooks(ReservationRequest reservationRequest);
    void validateBookAvailability(BookEntity bookEntity, BookRequest bookRequest);
}
