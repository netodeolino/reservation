package com.pt.reservation.application.ports.in;

import com.pt.reservation.application.exceptions.NotFoundException;
import com.pt.reservation.application.ports.out.cache.CachePort;
import com.pt.reservation.application.ports.out.database.ReservationDatabasePort;
import com.pt.reservation.application.ports.out.queue.ReservationQueuePort;
import com.pt.reservation.application.properties.ReservationProperties;
import com.pt.reservation.domain.dto.cache.CachedPage;
import com.pt.reservation.domain.dto.response.ReservationItemResponse;
import com.pt.reservation.domain.dto.response.ReservationResponse;
import com.pt.reservation.domain.entities.BookEntity;
import com.pt.reservation.domain.entities.ReservationEntity;
import com.pt.reservation.domain.entities.ReservationItemEntity;
import com.pt.reservation.domain.entities.UserEntity;
import com.pt.reservation.domain.enums.ReservationStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationUseCaseImplTest {

    @Mock
    private CachePort cachePort;

    @Mock
    private ReservationDatabasePort reservationDatabasePort;

    @Mock
    private ReservationItemUseCase reservationItemUseCase;

    @Mock
    private BookUseCase bookUseCase;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private ReservationQueuePort reservationQueuePort;

    @Mock
    private ReservationProperties reservationProperties;

    @InjectMocks
    private ReservationUseCaseImpl reservationUseCase;

    @BeforeEach
    void setUp() {
        reservationUseCase = new ReservationUseCaseImpl(
                cachePort, reservationDatabasePort, reservationItemUseCase,
                bookUseCase, userUseCase, reservationQueuePort, reservationProperties
        );
    }

    @Test
    void findByIdShouldReturnCachedReservationWhenExistsInCache() {
        Long reservationId = 1L;
        String cacheKey = "reservation:" + reservationId;
        List<ReservationItemResponse> items = Collections.singletonList(new ReservationItemResponse("Book test", 3));
        ReservationResponse cachedResponse = new ReservationResponse("Test", ReservationStatusEnum.CONFIRMED.name(), items);

        when(cachePort.get(cacheKey, ReservationResponse.class)).thenReturn(cachedResponse);

        ReservationResponse response = reservationUseCase.findById(reservationId);

        assertEquals(cachedResponse, response);
        verify(reservationDatabasePort, never()).findById(anyLong());
    }

    @Test
    void findByIdShouldFetchFromDatabaseAndCacheWhenNotInCache() {
        Long reservationId = 1L;
        String cacheKey = "reservation:" + reservationId;
        ReservationEntity reservationEntity = buildTestReservationEntity(reservationId, ReservationStatusEnum.CONFIRMED, null, null);

        when(cachePort.get(cacheKey, ReservationResponse.class)).thenReturn(null);
        when(reservationDatabasePort.findById(reservationId)).thenReturn(Optional.of(reservationEntity));

        ReservationResponse response = reservationUseCase.findById(reservationId);

        assertNotNull(response);
        verify(cachePort).save(cacheKey, response);
    }

    @Test
    void findAllByUserShouldReturnCachedResultsWhenCacheExists() {
        Long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String cacheKey = "reservations:user:" + userId + ":page:" + pageRequest.getPageNumber();
        List<ReservationItemResponse> items = Collections.singletonList(new ReservationItemResponse("Book test", 3));
        Page<ReservationResponse> page = new PageImpl<>(
                List.of(new ReservationResponse("Test", ReservationStatusEnum.CONFIRMED.name(), items)), pageRequest, 1);
        CachedPage<ReservationResponse> cachedPage = new CachedPage<>(page);

        when(cachePort.get(cacheKey, CachedPage.class)).thenReturn(cachedPage);

        Page<ReservationResponse> response = reservationUseCase.findAllByUser(userId, true, pageRequest);

        assertFalse(response.isEmpty());
        verify(reservationDatabasePort, never()).findAllByUserId(anyLong(), any());
    }

    @Test
    void findAllByUserShouldFetchFromDatabaseAndCacheWhenNotInCache() {
        Long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        String cacheKey = "reservations:user:" + userId + ":page:" + pageRequest.getPageNumber();
        Page<ReservationEntity> dbResponse = new PageImpl<>(
                List.of(buildTestReservationEntity(1L, ReservationStatusEnum.PENDING, null, null)), pageRequest, 1);

        when(cachePort.get(cacheKey, CachedPage.class)).thenReturn(null);
        when(reservationDatabasePort.findAllByUserId(userId, pageRequest)).thenReturn(dbResponse);

        Page<ReservationResponse> response = reservationUseCase.findAllByUser(userId, true, pageRequest);

        assertFalse(response.isEmpty());
        verify(cachePort).save(eq(cacheKey), any(CachedPage.class));
    }

    @Test
    void cancelShouldThrowExceptionWhenReservationNotFound() {
        Long reservationId = 1L;
        when(reservationDatabasePort.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> reservationUseCase.cancel(reservationId));
    }

    @Test
    void cancelShouldUpdateStatusAndUpdateCacheWhenValid() {
        Long reservationId = 1L;
        ReservationEntity reservationEntity = buildTestReservationEntity(reservationId, ReservationStatusEnum.PENDING, null, null);

        when(reservationDatabasePort.findById(reservationId)).thenReturn(Optional.of(reservationEntity));

        reservationUseCase.cancel(reservationId);

        assertEquals(ReservationStatusEnum.CANCELED, reservationEntity.getStatus());
        verify(reservationDatabasePort).save(reservationEntity);
        verify(cachePort).delete("reservation:" + reservationId);
    }

    @Test
    void pickUpShouldThrowExceptionWhenReservationNotFound() {
        Long reservationId = 1L;
        when(reservationDatabasePort.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservationUseCase.pickUp(reservationId));
    }

    @Test
    void pickUpShouldUpdateStatusWhenValid() {
        Long reservationId = 1L;
        LocalDateTime expirationInOneDay = LocalDateTime.now().plusDays(1);
        List<ReservationItemEntity> items = Collections.singletonList(ReservationItemEntity.builder()
                        .book(BookEntity.builder().build())
                .build());
        ReservationEntity reservationEntity = buildTestReservationEntity(reservationId, ReservationStatusEnum.PENDING, expirationInOneDay, items);

        when(reservationDatabasePort.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(reservationDatabasePort.save(any())).thenReturn(reservationEntity);

        ReservationResponse response = reservationUseCase.pickUp(reservationId);

        assertEquals(ReservationStatusEnum.CONFIRMED, reservationEntity.getStatus());
        assertNotNull(response);
        verify(reservationDatabasePort).save(reservationEntity);
    }

    private ReservationEntity buildTestReservationEntity(Long id, ReservationStatusEnum status, LocalDateTime expiresAt, List<ReservationItemEntity> items) {
        return ReservationEntity.builder()
                .id(id)
                .user(UserEntity.builder().build())
                .items(items != null ? items : Collections.emptyList())
                .status(status)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .pickedUpAt(null)
                .build();
    }
}
