package com.pt.reservation.application.ports.in;

import com.pt.reservation.application.ports.out.database.BookDatabasePort;
import com.pt.reservation.domain.entities.BookEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookUseCaseImplTest {

    @Mock
    private BookDatabasePort bookDatabasePort;

    @InjectMocks
    private BookUseCaseImpl bookUseCase;

    @Test
    void findByIdShouldReturnBookWhenFound() {
        Long bookId = 1L;
        BookEntity bookEntity = BookEntity.builder()
                .id(bookId)
                .title("Test")
                .build();

        when(bookDatabasePort.findById(bookId)).thenReturn(Optional.of(bookEntity));

        BookEntity result = bookUseCase.findById(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        verify(bookDatabasePort).findById(bookId);
    }

    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        Long bookId = 1L;
        when(bookDatabasePort.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> bookUseCase.findById(bookId));

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void saveShouldReturnSavedBook() {
        String title = "Test book";
        BookEntity bookEntity = BookEntity.builder()
                .title(title)
                .build();

        when(bookDatabasePort.save(bookEntity)).thenReturn(bookEntity);

        BookEntity result = bookUseCase.save(bookEntity);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(bookDatabasePort).save(bookEntity);
    }
}
