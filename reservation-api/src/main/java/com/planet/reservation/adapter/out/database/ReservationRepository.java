package com.planet.reservation.adapter.out.database;

import com.planet.reservation.application.ports.out.ReservationDatabasePort;
import com.planet.reservation.domain.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, ReservationDatabasePort {
}
