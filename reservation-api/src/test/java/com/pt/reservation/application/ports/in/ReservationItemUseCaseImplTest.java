package com.pt.reservation.application.ports.in;

import com.pt.reservation.application.exceptions.ConflictException;
import com.pt.reservation.application.ports.out.database.ReservationItemDatabasePort;
import com.pt.reservation.domain.dto.request.BookRequest;
import com.pt.reservation.domain.dto.request.ReservationRequest;
import com.pt.reservation.domain.entities.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationItemUseCaseImplTest {

    @Mock
    private ReservationItemDatabasePort reservationItemDatabasePort;

    @InjectMocks
    private ReservationItemUseCaseImpl reservationItemUseCase;

    @BeforeEach
    void setUp() {
        reservationItemUseCase = new ReservationItemUseCaseImpl(reservationItemDatabasePort);
    }

    @Test
    void validateNumberOfBooksShouldThrowConflictExceptionWhenUserExceedsMaxBooks() {
        ReservationRequest reservationRequest = new ReservationRequest(1L, List.of(
                new BookRequest(1L, 2),
                new BookRequest(2L, 2)
        ));

        when(reservationItemDatabasePort.countActiveBooksByUser(1L)).thenReturn(2);

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            reservationItemUseCase.validateNumberOfBooks(reservationRequest);
        });

        assertEquals("User has exceeded max number of books: 3", exception.getMessage());
    }

    @Test
    void validateNumberOfBooksShouldNotThrowExceptionWhenUserIsWithinLimit() {
        ReservationRequest reservationRequest = new ReservationRequest(1L, List.of(
                new BookRequest(1L, 1)
        ));

        when(reservationItemDatabasePort.countActiveBooksByUser(1L)).thenReturn(1);

        assertDoesNotThrow(() -> reservationItemUseCase.validateNumberOfBooks(reservationRequest));
    }

    @Test
    void validateBookAvailabilityShouldThrowIllegalArgumentExceptionWhenNotEnoughStock() {
        BookEntity bookEntity = buildTestBookEntity(5);
        BookRequest bookRequest = new BookRequest(1L, 3);

        when(reservationItemDatabasePort.countReservedBooksByBook(1L)).thenReturn(4);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationItemUseCase.validateBookAvailability(bookEntity, bookRequest);
        });

        assertEquals("Book 'Title test' does not have enough stock for reservation", exception.getMessage());
    }

    @Test
    void validateBookAvailabilityShouldNotThrowExceptionWhenStockIsSufficient() {
        BookEntity bookEntity = buildTestBookEntity(10);
        BookRequest bookRequest = new BookRequest(1L, 2);

        when(reservationItemDatabasePort.countReservedBooksByBook(1L)).thenReturn(5);

        assertDoesNotThrow(() -> reservationItemUseCase.validateBookAvailability(bookEntity, bookRequest));
    }

    private BookEntity buildTestBookEntity(int totalAvailable) {
        return BookEntity.builder()
                .id(1L)
                .title("Title test")
                .author("Author test")
                .isbn("ISBN test")
                .totalCopies(10)
                .totalAvailable(totalAvailable)
                .version(1)
                .build();
    }
}
