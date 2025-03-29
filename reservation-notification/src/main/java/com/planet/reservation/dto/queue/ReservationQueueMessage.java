package com.planet.reservation.dto.queue;

import java.util.List;

public record ReservationQueueMessage(UserQueueMessage user, List<BookQueueMessage> books, String status) {}
