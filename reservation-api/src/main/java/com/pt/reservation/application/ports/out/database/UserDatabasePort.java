package com.pt.reservation.application.ports.out.database;

import com.pt.reservation.domain.entities.UserEntity;

import java.util.Optional;

public interface UserDatabasePort {
    Optional<UserEntity> findById(Long userId);
}
