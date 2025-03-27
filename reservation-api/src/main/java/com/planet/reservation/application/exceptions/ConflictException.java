package com.planet.reservation.application.exceptions;

public class ConflictException extends RuntimeException {
    public ConflictException(String exception) {
        super(exception);
    }
}
