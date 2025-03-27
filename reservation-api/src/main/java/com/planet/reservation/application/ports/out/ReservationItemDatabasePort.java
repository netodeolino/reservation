package com.planet.reservation.application.ports.out;

public interface ReservationItemDatabasePort {
    int countActiveBooksByUser(Long userId);
    int countReservedBooksByBook(Long bookId);
}
