package com.pt.reservation.domain.dto.response;

import com.pt.reservation.domain.entities.ReservationEntity;

import java.util.List;

public record ReservationResponse(String user, String status, List<ReservationItemResponse> items) {

    public static ReservationResponse fromEntity(ReservationEntity entity) {
        List<ReservationItemResponse> itemsResponse = entity.getItems().stream().map(ReservationItemResponse::fromEntity).toList();
        return new ReservationResponse(entity.getUser().getName(), entity.getStatus().name(), itemsResponse);
    }
}
