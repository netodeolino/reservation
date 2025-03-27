package com.planet.reservation.application.ports.in;

import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReservationUseCase {

    ReservationResponse findById(Long reservationId);
    Page<ReservationResponse> findAllByUser(Long userId, PageRequest pageRequest);
    ReservationResponse reservation(ReservationRequest reservationRequest);
    void cancel(Long reservationId);
    ReservationResponse pickUp(Long reservationId);

}
