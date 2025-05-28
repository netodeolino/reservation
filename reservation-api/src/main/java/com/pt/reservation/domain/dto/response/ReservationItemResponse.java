package com.pt.reservation.domain.dto.response;

import com.pt.reservation.domain.entities.ReservationItemEntity;

public record ReservationItemResponse(String book, int quantity) {

    public static ReservationItemResponse fromEntity(ReservationItemEntity entity) {
        return new ReservationItemResponse(entity.getBook().getTitle(), entity.getQuantity());
    }
}
