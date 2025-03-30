package com.planet.reservation.adapter.in.rest;

import com.planet.reservation.domain.dto.request.ReservationRequest;
import com.planet.reservation.domain.dto.response.ApiResponse;
import com.planet.reservation.domain.dto.response.PaginationResponse;
import com.planet.reservation.domain.dto.response.ReservationResponse;
import com.planet.reservation.application.ports.in.ReservationUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/reservations")
public class ReservationEndpoint {
    private final ReservationUseCase reservationUseCase;

    public ReservationEndpoint(ReservationUseCase reservationUseCase) {
        this.reservationUseCase = reservationUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> reserve(@RequestBody ReservationRequest reservationRequest) {
        ReservationResponse response = reservationUseCase.reservation(reservationRequest);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.CREATED.value(), HttpStatus.CREATED.name(), Collections.singletonList(response), null));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> findById(@PathVariable("reservationId") Long reservationId) {
        ReservationResponse response = reservationUseCase.findById(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), Collections.singletonList(response), null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> findAllByUser(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "allStatus", defaultValue = "false") Boolean allStatus
    ) {
        Page<ReservationResponse> reservationResponsePage = reservationUseCase.findAllByUser(userId, allStatus, PageRequest.of(page, pageSize));
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), HttpStatus.OK.name(), reservationResponsePage.getContent(), PaginationResponse.fromPage(reservationResponsePage)));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable("reservationId") Long reservationId) {
        reservationUseCase.cancel(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.ACCEPTED.value(), HttpStatus.ACCEPTED.name(), Collections.singletonList("Canceled"), null));
    }

    @GetMapping("/pickup/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> pickUp(@PathVariable("reservationId") Long reservationId) {
        ReservationResponse response = reservationUseCase.pickUp(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), Collections.singletonList(response), null));
    }

}
