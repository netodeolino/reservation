package com.pt.reservation.domain.dto.queue;

import com.pt.reservation.domain.entities.BookEntity;

public record BookQueueMessage(String title, String author) {
    public static BookQueueMessage fromEntity(BookEntity bookEntity) {
        return new BookQueueMessage(bookEntity.getTitle(), bookEntity.getAuthor());
    }
}
