package com.pt.reservation.application.exceptions;

public class UnprocessableException extends RuntimeException {
    public UnprocessableException(String exception) {
        super(exception);
    }
}
