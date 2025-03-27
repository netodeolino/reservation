package com.planet.reservation.application.ports.in;

import com.planet.reservation.application.exceptions.ConflictException;
import com.planet.reservation.application.exceptions.NotFoundException;
import com.planet.reservation.application.exceptions.UnprocessableException;
import com.planet.reservation.application.ports.out.BookDatabasePort;
import com.planet.reservation.application.ports.out.ReservationDatabasePort;
import com.planet.reservation.application.ports.out.ReservationItemDatabasePort;
import com.planet.reservation.application.ports.out.UserDatabasePort;
import com.planet.reservation.domain.dto.request.BookRequest;
import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.dto.response.ReservationResponse;
import com.planet.reservation.domain.entities.BookEntity;
import com.planet.reservation.domain.entities.ReservationEntity;
import com.planet.reservation.domain.entities.ReservationItemEntity;
import com.planet.reservation.domain.entities.UserEntity;
import com.planet.reservation.domain.enums.ReservationStatusEnum;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationUseCaseImpl implements ReservationUseCase {
    private final ReservationDatabasePort reservationDatabasePort;
    private final ReservationItemDatabasePort reservationItemDatabasePort;
    private final BookDatabasePort bookDatabasePort;
    private final UserDatabasePort userDatabasePort;

    private static final Logger log = LogManager.getLogger(ReservationUseCaseImpl.class);

    private static final int MAX_NUMBER_OF_BOOKS_AT_TIME = 3;
    private static final int MAX_RESERVATION_ATTEMPTS = 3;
    private static final int DAYS_TO_EXPIRE_RESERVATION = 7;

    public ReservationUseCaseImpl(
            ReservationDatabasePort reservationDatabasePort,
            ReservationItemDatabasePort reservationItemDatabasePort,
            BookDatabasePort bookDatabasePort,
            UserDatabasePort userDatabasePort
    ) {
        this.reservationDatabasePort = reservationDatabasePort;
        this.reservationItemDatabasePort = reservationItemDatabasePort;
        this.bookDatabasePort = bookDatabasePort;
        this.userDatabasePort = userDatabasePort;
    }

    @Override
    public ReservationResponse findById(Long reservationId) {
        return reservationDatabasePort.findById(reservationId)
                .map(ReservationResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }

    @Override
    public Page<ReservationResponse> findAllByUser(Long userId, PageRequest pageRequest) {
        return reservationDatabasePort.findAllByUserId(userId, pageRequest)
                .map(ReservationResponse::fromEntity);
    }

    @Override
    @Transactional
    public ReservationResponse reservation(ReservationRequest reservationRequest) {
        for (int attempt = 1; attempt <= MAX_RESERVATION_ATTEMPTS; attempt++) {
            try {
                return executeReservation(reservationRequest);
            } catch (OptimisticLockException e) {
                if (attempt == MAX_RESERVATION_ATTEMPTS) {
                    throw new UnprocessableException("Failed to reserve books after multiple attempts. Please try again.");
                }
            }
        }
        throw new IllegalStateException("Unexpected flow in reservation method");
    }

    @Override
    @Transactional
    public void cancel(Long reservationId) {
        ReservationEntity reservationEntity = reservationDatabasePort.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (reservationEntity.getStatus() == ReservationStatusEnum.CANCELED) {
            throw new UnprocessableException("Reservation is already cancelled");
        }

        reservationEntity.setStatus(ReservationStatusEnum.CANCELED);
        reservationDatabasePort.save(reservationEntity);

        for (ReservationItemEntity reservationItemEntity : reservationEntity.getItems()) {
            BookEntity bookEntity = reservationItemEntity.getBook();
            bookEntity.setTotalAvailable(bookEntity.getTotalAvailable() + reservationItemEntity.getQuantity());
            bookDatabasePort.save(bookEntity);
        }
    }

    @Override
    @Transactional
    public ReservationResponse pickUp(Long reservationId) {
        ReservationEntity reservationEntity = reservationDatabasePort.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        if (reservationEntity.getStatus() != ReservationStatusEnum.PENDING) {
            throw new IllegalStateException("Only pending reservations can be picked");
        }

        if (reservationEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The reservation is already expired");
        }

        reservationEntity.setStatus(ReservationStatusEnum.CONFIRMED);
        reservationEntity.setPickedUpAt(LocalDateTime.now());

        ReservationEntity savedReservationEntity = reservationDatabasePort.save(reservationEntity);
        return ReservationResponse.fromEntity(savedReservationEntity);
    }

    private ReservationResponse executeReservation(ReservationRequest reservationRequest) {
        validateNumberOfBooks(reservationRequest);

        UserEntity userEntity = findUserOrThrow(reservationRequest.userId());
        ReservationEntity reservationEntity = createInitialReservation(userEntity);

        processReservationItems(reservationRequest, reservationEntity);

        ReservationEntity savedReservationEntity = reservationDatabasePort.save(reservationEntity);
        return ReservationResponse.fromEntity(savedReservationEntity);
    }

    private UserEntity findUserOrThrow(Long userId) {
        return userDatabasePort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private ReservationEntity createInitialReservation(UserEntity userEntity) {
        LocalDateTime timeNow = LocalDateTime.now();
        return ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.PENDING)
                .createdAt(timeNow)
                .expiresAt(timeNow.plusDays(DAYS_TO_EXPIRE_RESERVATION))
                .build();
    }

    private void processReservationItems(ReservationRequest reservationRequest, ReservationEntity reservation) {
        List<ReservationItemEntity> reservationItems = reservationRequest.books().stream()
                .map(bookRequest -> processBookReservation(bookRequest, reservation))
                .toList();

        reservation.setItems(reservationItems);
    }

    private ReservationItemEntity processBookReservation(BookRequest bookRequest, ReservationEntity reservation) {
        BookEntity bookEntity = findBookOrThrow(bookRequest.bookId());

        validateBookAvailability(bookEntity, bookRequest);
        updateBookAvailability(bookEntity, bookRequest.quantity());

        return buildReservationItem(bookEntity, reservation, bookRequest.quantity());
    }

    private BookEntity findBookOrThrow(Long bookId) {
        return bookDatabasePort.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    private void updateBookAvailability(BookEntity bookEntity, int quantity) {
        bookEntity.setTotalAvailable(bookEntity.getTotalAvailable() - quantity);
        bookDatabasePort.save(bookEntity);
    }

    private ReservationItemEntity buildReservationItem(BookEntity bookEntity, ReservationEntity reservation, int quantity) {
        return ReservationItemEntity.builder()
                .book(bookEntity)
                .reservation(reservation)
                .quantity(quantity)
                .build();
    }

    private void validateNumberOfBooks(ReservationRequest reservationRequest) {
        int currentReservations = reservationItemDatabasePort.countActiveBooksByUser(reservationRequest.userId());

        int quantityRequested = reservationRequest.books().stream()
                .mapToInt(BookRequest::quantity)
                .sum();

        if (quantityRequested + currentReservations > MAX_NUMBER_OF_BOOKS_AT_TIME) {
            throw new ConflictException("User has exceeded max number of books: " + MAX_NUMBER_OF_BOOKS_AT_TIME);
        }
    }

    private void validateBookAvailability(BookEntity bookEntity, BookRequest bookRequest) {
        int reservedCount = reservationItemDatabasePort.countReservedBooksByBook(bookEntity.getId());
        int availableToReserve = bookEntity.getTotalAvailable() - reservedCount;

        if (bookRequest.quantity() > availableToReserve) {
            throw new IllegalArgumentException("Book '" + bookEntity.getTitle() + "' does not have enough stock for reservation");
        }
    }

}
