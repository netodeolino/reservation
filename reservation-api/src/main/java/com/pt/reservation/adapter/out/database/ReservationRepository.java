package com.pt.reservation.adapter.out.database;

import com.pt.reservation.application.ports.out.database.ReservationDatabasePort;
import com.pt.reservation.domain.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, ReservationDatabasePort {
}
