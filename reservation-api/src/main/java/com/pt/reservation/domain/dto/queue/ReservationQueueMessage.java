package com.pt.reservation.domain.dto.queue;

import com.pt.reservation.domain.entities.ReservationEntity;
import com.pt.reservation.domain.entities.ReservationItemEntity;

import java.util.List;

public record ReservationQueueMessage(UserQueueMessage user, List<BookQueueMessage> books, String status) {

    public static ReservationQueueMessage fromEntity(ReservationEntity reservationEntity) {
        UserQueueMessage userQueueMessage = UserQueueMessage.fromEntity(reservationEntity.getUser());
        String status = reservationEntity.getStatus().name();
        List<BookQueueMessage> books = reservationEntity.getItems().stream()
                .map(ReservationItemEntity::getBook)
                .map(BookQueueMessage::fromEntity)
                .toList();
        return new ReservationQueueMessage(userQueueMessage, books, status);
    }
}
