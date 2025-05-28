package com.pt.reservation.application.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String exception) {
        super(exception);
    }
}
