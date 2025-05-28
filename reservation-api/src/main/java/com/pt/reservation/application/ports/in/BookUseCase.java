package com.pt.reservation.application.ports.in;

import com.pt.reservation.domain.entities.BookEntity;

public interface BookUseCase {
    BookEntity findById(Long bookId);
    BookEntity save(BookEntity bookEntity);
}
