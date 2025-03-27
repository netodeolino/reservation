package com.planet.reservation.application.ports.out;

import com.planet.reservation.domain.entities.UserEntity;

import java.util.Optional;

public interface UserDatabasePort {
    Optional<UserEntity> findById(Long userId);
}
