package com.pt.reservation.application.ports.in;

import com.pt.reservation.application.ports.out.database.BookDatabasePort;
import com.pt.reservation.domain.entities.BookEntity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookUseCaseImpl implements BookUseCase {
    private final BookDatabasePort bookDatabasePort;

    public BookUseCaseImpl(BookDatabasePort bookDatabasePort) {
        this.bookDatabasePort = bookDatabasePort;
    }

    @Override
    public BookEntity findById(Long bookId) {
        return bookDatabasePort.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    @Override
    public BookEntity save(BookEntity bookEntity) {
        return bookDatabasePort.save(bookEntity);
    }

}
