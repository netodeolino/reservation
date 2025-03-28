package com.planet.reservation.application.ports.in;

import com.planet.reservation.application.exceptions.ConflictException;
import com.planet.reservation.application.ports.out.database.ReservationItemDatabasePort;
import com.planet.reservation.domain.dto.request.BookRequest;
import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.entities.BookEntity;
import org.springframework.stereotype.Service;

@Service
public class ReservationItemUseCaseImpl implements ReservationItemUseCase {
    private final ReservationItemDatabasePort reservationItemDatabasePort;

    private static final int MAX_NUMBER_OF_BOOKS_AT_TIME = 3;

    public ReservationItemUseCaseImpl(ReservationItemDatabasePort reservationItemDatabasePort) {
        this.reservationItemDatabasePort = reservationItemDatabasePort;
    }

    @Override
    public void validateNumberOfBooks(ReservationRequest reservationRequest) {
        int currentReservations = reservationItemDatabasePort.countActiveBooksByUser(reservationRequest.userId());

        int quantityRequested = reservationRequest.books().stream()
                .mapToInt(BookRequest::quantity)
                .sum();

        if (quantityRequested + currentReservations > MAX_NUMBER_OF_BOOKS_AT_TIME) {
            throw new ConflictException("User has exceeded max number of books: " + MAX_NUMBER_OF_BOOKS_AT_TIME);
        }
    }

    @Override
    public void validateBookAvailability(BookEntity bookEntity, BookRequest bookRequest) {
        int reservedCount = reservationItemDatabasePort.countReservedBooksByBook(bookEntity.getId());
        int availableToReserve = bookEntity.getTotalAvailable() - reservedCount;

        if (bookRequest.quantity() > availableToReserve) {
            throw new IllegalArgumentException("Book '" + bookEntity.getTitle() + "' does not have enough stock for reservation");
        }
    }
}
