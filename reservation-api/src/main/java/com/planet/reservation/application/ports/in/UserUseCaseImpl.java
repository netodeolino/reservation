package com.planet.reservation.application.ports.in;

import com.planet.reservation.application.exceptions.NotFoundException;
import com.planet.reservation.application.ports.out.UserDatabasePort;
import com.planet.reservation.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserUseCaseImpl implements UserUseCase {
    private final UserDatabasePort userDatabasePort;

    public UserUseCaseImpl(UserDatabasePort userDatabasePort) {
        this.userDatabasePort = userDatabasePort;
    }

    @Override
    public UserEntity findById(Long userId) {
        return userDatabasePort.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
