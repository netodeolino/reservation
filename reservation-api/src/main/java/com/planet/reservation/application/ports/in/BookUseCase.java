package com.planet.reservation.application.ports.in;

import com.planet.reservation.domain.entities.BookEntity;

public interface BookUseCase {
    BookEntity findById(Long bookId);
    BookEntity save(BookEntity bookEntity);
}
