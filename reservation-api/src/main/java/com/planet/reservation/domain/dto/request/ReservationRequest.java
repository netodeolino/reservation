package com.planet.reservation.domain.dto.request;

import java.util.List;

public record ReservationRequest(Long userId, List<BookRequest> books) {}
