package com.pt.reservation.application.ports.out.database;

public interface ReservationItemDatabasePort {
    int countActiveBooksByUser(Long userId);
    int countReservedBooksByBook(Long bookId);
}
