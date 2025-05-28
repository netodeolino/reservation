package com.pt.reservation.application.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String exception) {
        super(exception);
    }
}
