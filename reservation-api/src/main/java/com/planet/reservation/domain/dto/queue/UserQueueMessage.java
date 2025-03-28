package com.planet.reservation.domain.dto.queue;

import com.planet.reservation.domain.entities.UserEntity;

public record UserQueueMessage(String name, String email) {
    public static UserQueueMessage fromEntity(UserEntity userEntity) {
        return new UserQueueMessage(userEntity.getName(), userEntity.getEmail());
    }
}
