package com.pt.reservation.application.ports.in;

import com.pt.reservation.application.exceptions.NotFoundException;
import com.pt.reservation.application.ports.out.database.UserDatabasePort;
import com.pt.reservation.domain.entities.UserEntity;
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
