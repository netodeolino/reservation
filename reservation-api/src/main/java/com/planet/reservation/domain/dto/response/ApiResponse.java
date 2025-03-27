package com.planet.reservation.domain.dto.response;

import java.util.List;

public record ApiResponse<T>(Integer code, String message, List<T> data, PaginationResponse pagination) {}
