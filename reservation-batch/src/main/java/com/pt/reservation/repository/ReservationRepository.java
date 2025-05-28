package com.pt.reservation.repository;

import com.pt.reservation.domain.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Modifying
    @Query(value = "UPDATE reservations SET status = 'EXPIRED' WHERE status <> 'EXPIRED' AND picked_up_at IS NULL AND created_at <= :datePeriod",
            nativeQuery = true)
    int markReservationsAsExpired(@Param("datePeriod") LocalDateTime datePeriod);
}
