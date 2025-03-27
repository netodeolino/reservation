package com.planet.reservation.application.ports.out;

import com.planet.reservation.domain.entities.BookEntity;

import java.util.Optional;

public interface BookDatabasePort {
    Optional<BookEntity> findById(Long bookId);
    BookEntity save(BookEntity bookEntity);
}
