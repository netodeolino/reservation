package com.pt.reservation.application.ports.in;

import com.pt.reservation.domain.dto.request.ReservationRequest;
import com.pt.reservation.domain.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReservationUseCase {

    ReservationResponse findById(Long reservationId);
    Page<ReservationResponse> findAllByUser(Long userId, Boolean allStatus, PageRequest pageRequest);
    ReservationResponse reservation(ReservationRequest reservationRequest);
    void cancel(Long reservationId);
    ReservationResponse pickUp(Long reservationId);

}
