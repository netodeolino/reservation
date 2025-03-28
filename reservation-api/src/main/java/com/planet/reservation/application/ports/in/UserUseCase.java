package com.planet.reservation.application.ports.in;

import com.planet.reservation.domain.entities.UserEntity;

public interface UserUseCase {
    UserEntity findById(Long userId);
}
