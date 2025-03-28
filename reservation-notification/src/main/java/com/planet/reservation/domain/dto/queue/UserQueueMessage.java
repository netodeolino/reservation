package com.planet.reservation.domain.dto.queue;

import com.planet.reservation.domain.entities.UserEntity;

public record UserQueueMessage(Long id, String name, String email) {
    public static UserQueueMessage fromEntity(UserEntity userEntity) {
        return new UserQueueMessage(userEntity.getId(), userEntity.getName(), userEntity.getEmail());
    }
}
