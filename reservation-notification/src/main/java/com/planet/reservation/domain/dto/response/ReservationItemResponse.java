package com.planet.reservation.domain.dto.response;

import com.planet.reservation.domain.entities.ReservationItemEntity;

public record ReservationItemResponse(String book, int quantity) {

    public static ReservationItemResponse fromEntity(ReservationItemEntity entity) {
        return new ReservationItemResponse(entity.getBook().getTitle(), entity.getQuantity());
    }
}
