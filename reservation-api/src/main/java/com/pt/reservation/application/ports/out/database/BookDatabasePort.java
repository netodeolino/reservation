package com.pt.reservation.application.ports.out.database;

import com.pt.reservation.domain.entities.BookEntity;

import java.util.Optional;

public interface BookDatabasePort {
    Optional<BookEntity> findById(Long bookId);
    BookEntity save(BookEntity bookEntity);
}
