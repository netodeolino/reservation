package com.planet.reservation.scheduling;

import com.planet.reservation.properties.SchedulingProperties;
import com.planet.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotPickedUpReservationsScheduledTaskTest {
    @Mock
    private SchedulingProperties schedulingProperties;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SchedulingProperties.NotPickedUpReservationFixed notPickedUpReservationFixed;

    @InjectMocks
    private NotPickedUpReservationsScheduledTask task;

    @BeforeEach
    void setUp() {
        when(schedulingProperties.getNotPickedUpReservationFixed()).thenReturn(notPickedUpReservationFixed);
        when(notPickedUpReservationFixed.getExpirationTime()).thenReturn(3);
    }

    @Test
    void executeShouldCallRepositoryAndLogResult() {
        when(reservationRepository.markReservationsAsExpired(any(LocalDateTime.class))).thenReturn(5);

        task.execute();

        verify(reservationRepository).markReservationsAsExpired(any(LocalDateTime.class));
        verify(reservationRepository).markReservationsAsExpired(argThat(date -> date.isBefore(LocalDateTime.now())));

        assertDoesNotThrow(() -> task.execute());
    }
}
