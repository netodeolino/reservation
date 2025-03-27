package com.planet.reservation.adapter.out.database;

import com.planet.reservation.application.ports.out.UserDatabasePort;
import com.planet.reservation.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserDatabasePort {
}
