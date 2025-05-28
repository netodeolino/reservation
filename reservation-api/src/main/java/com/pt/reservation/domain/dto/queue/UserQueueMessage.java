package com.pt.reservation.domain.dto.queue;

import com.pt.reservation.domain.entities.UserEntity;

public record UserQueueMessage(Long id, String name, String email) {
    public static UserQueueMessage fromEntity(UserEntity userEntity) {
        return new UserQueueMessage(userEntity.getId(), userEntity.getName(), userEntity.getEmail());
    }
}
