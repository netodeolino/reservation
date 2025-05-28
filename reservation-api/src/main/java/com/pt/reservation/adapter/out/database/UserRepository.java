package com.pt.reservation.adapter.out.database;

import com.pt.reservation.application.ports.out.database.UserDatabasePort;
import com.pt.reservation.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserDatabasePort {
}
