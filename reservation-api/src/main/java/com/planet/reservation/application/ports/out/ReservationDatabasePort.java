package com.planet.reservation.application.ports.out;

import com.planet.reservation.domain.entities.ReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface ReservationDatabasePort {
    Optional<ReservationEntity> findById(Long id);
    Page<ReservationEntity> findAllByUserId(Long userId, PageRequest pageRequest);
    ReservationEntity save(ReservationEntity reservationEntity);
}
