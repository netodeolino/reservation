package com.pt.reservation.scheduling;

import com.pt.reservation.properties.SchedulingProperties;
import com.pt.reservation.repository.ReservationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class NotPickedUpReservationsScheduledTask implements ScheduledTask {
    private final SchedulingProperties schedulingProperties;
    private final ReservationRepository reservationRepository;

    private static final Logger log = LogManager.getLogger(NotPickedUpReservationsScheduledTask.class);

    public NotPickedUpReservationsScheduledTask(SchedulingProperties schedulingProperties, ReservationRepository reservationRepository) {
        this.schedulingProperties = schedulingProperties;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${app.scheduling.notPickedUpReservationFixed.delayTime}")
    public void execute() {
        int expirationTime = schedulingProperties.getNotPickedUpReservationFixed().getExpirationTime();
        int updatedCount = reservationRepository.markReservationsAsExpired(LocalDateTime.now().minusDays(expirationTime));
        log.info("Reservations expired {}", updatedCount);
    }
}
