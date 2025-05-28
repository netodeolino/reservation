package com.pt.reservation.application.ports.in;

import com.pt.reservation.domain.entities.UserEntity;

public interface UserUseCase {
    UserEntity findById(Long userId);
}
