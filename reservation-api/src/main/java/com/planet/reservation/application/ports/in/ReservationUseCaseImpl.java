package com.planet.reservation.application.ports.in;

import com.planet.reservation.application.exceptions.NotFoundException;
import com.planet.reservation.application.exceptions.UnprocessableException;
import com.planet.reservation.application.ports.out.cache.CachePort;
import com.planet.reservation.application.ports.out.database.ReservationDatabasePort;
import com.planet.reservation.application.ports.out.queue.ReservationQueuePort;
import com.planet.reservation.application.properties.ReservationProperties;
import com.planet.reservation.domain.dto.cache.CachedPage;
import com.planet.reservation.domain.dto.queue.ReservationQueueMessage;
import com.planet.reservation.domain.dto.request.BookRequest;
import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.dto.response.ReservationResponse;
import com.planet.reservation.domain.entities.BookEntity;
import com.planet.reservation.domain.entities.ReservationEntity;
import com.planet.reservation.domain.entities.ReservationItemEntity;
import com.planet.reservation.domain.entities.UserEntity;
import com.planet.reservation.domain.enums.ReservationStatusEnum;
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
    private final CachePort cachePort;
    private final ReservationDatabasePort reservationDatabasePort;
    private final ReservationItemUseCase reservationItemUseCase;
    private final BookUseCase bookUseCase;
    private final UserUseCase userUseCase;
    private final ReservationQueuePort reservationQueuePort;
    private final ReservationProperties reservationProperties;

    private static final Logger log = LogManager.getLogger(ReservationUseCaseImpl.class);

    private static final int MAX_RESERVATION_ATTEMPTS = 3;

    public ReservationUseCaseImpl(
            CachePort cachePort,
            ReservationDatabasePort reservationDatabasePort,
            ReservationItemUseCase reservationItemUseCase,
            BookUseCase bookUseCase,
            UserUseCase userUseCase,
            ReservationQueuePort reservationQueuePort,
            ReservationProperties reservationProperties
    ) {
        this.cachePort = cachePort;
        this.reservationDatabasePort = reservationDatabasePort;
        this.reservationItemUseCase = reservationItemUseCase;
        this.bookUseCase = bookUseCase;
        this.userUseCase = userUseCase;
        this.reservationQueuePort = reservationQueuePort;
        this.reservationProperties = reservationProperties;
    }

    @Override
    public ReservationResponse findById(Long reservationId) {
        String cacheKey = "reservation:" + reservationId;

        ReservationResponse cachedReservation = cachePort.get(cacheKey, ReservationResponse.class);
        if (cachedReservation != null) {
            return cachedReservation;
        }

        ReservationResponse reservation = reservationDatabasePort.findById(reservationId)
                .map(ReservationResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        cachePort.save(cacheKey, reservation);

        return reservation;
    }

    @Override
    public Page<ReservationResponse> findAllByUser(Long userId, PageRequest pageRequest) {
        String cacheKey = "reservations:user:" + userId + ":page:" + pageRequest.getPageNumber();

        CachedPage<ReservationResponse> cachedPage = cachePort.get(cacheKey, CachedPage.class);
        if (cachedPage != null) {
            return cachedPage.toPage();
        }

        Page<ReservationResponse> reservations = reservationDatabasePort.findAllByUserId(userId, pageRequest)
                .map(ReservationResponse::fromEntity);

        cachePort.save(cacheKey, new CachedPage<>(reservations));

        return reservations;
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
            bookUseCase.save(bookEntity);
        }

        cachePort.delete("reservation:" + reservationId);
        cachePort.delete("reservations:user:" + reservationEntity.getUser().getId() + ":page:*");

        log.info("Canceled the reservation id {}", reservationId);
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

        cachePort.delete("reservation:" + reservationId);
        cachePort.delete("reservations:user:" + reservationEntity.getUser().getId() + ":page:*");

        return ReservationResponse.fromEntity(savedReservationEntity);
    }

    private ReservationResponse executeReservation(ReservationRequest reservationRequest) {
        reservationItemUseCase.validateNumberOfBooks(reservationRequest);

        UserEntity userEntity = userUseCase.findById(reservationRequest.userId());
        ReservationEntity reservationEntity = createInitialReservation(userEntity);

        processReservationItems(reservationRequest, reservationEntity);

        ReservationEntity savedReservationEntity = reservationDatabasePort.save(reservationEntity);
        log.info("Saved reservation id {} of the user id {}", savedReservationEntity.getId(), savedReservationEntity.getUser().getId());

        reservationQueuePort.send(ReservationQueueMessage.fromEntity(savedReservationEntity));

        return ReservationResponse.fromEntity(savedReservationEntity);
    }

    private ReservationEntity createInitialReservation(UserEntity userEntity) {
        LocalDateTime timeNow = LocalDateTime.now();
        int daysToExpireReservation = reservationProperties.getExpirationTime();
        return ReservationEntity.builder()
                .user(userEntity)
                .status(ReservationStatusEnum.PENDING)
                .createdAt(timeNow)
                .expiresAt(timeNow.plusDays(daysToExpireReservation))
                .build();
    }

    private void processReservationItems(ReservationRequest reservationRequest, ReservationEntity reservation) {
        List<ReservationItemEntity> reservationItems = reservationRequest.books().stream()
                .map(bookRequest -> processBookReservation(bookRequest, reservation))
                .toList();

        reservation.setItems(reservationItems);
    }

    private ReservationItemEntity processBookReservation(BookRequest bookRequest, ReservationEntity reservation) {
        BookEntity bookEntity = bookUseCase.findById(bookRequest.bookId());

        reservationItemUseCase.validateBookAvailability(bookEntity, bookRequest);
        updateBookAvailability(bookEntity, bookRequest.quantity());

        return buildReservationItem(bookEntity, reservation, bookRequest.quantity());
    }

    private void updateBookAvailability(BookEntity bookEntity, int quantity) {
        bookEntity.setTotalAvailable(bookEntity.getTotalAvailable() - quantity);
        bookUseCase.save(bookEntity);
    }

    private ReservationItemEntity buildReservationItem(BookEntity bookEntity, ReservationEntity reservation, int quantity) {
        return ReservationItemEntity.builder()
                .book(bookEntity)
                .reservation(reservation)
                .quantity(quantity)
                .build();
    }

}
