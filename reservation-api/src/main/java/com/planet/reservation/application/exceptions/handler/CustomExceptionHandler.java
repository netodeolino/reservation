package com.planet.reservation.application.exceptions.handler;

import com.planet.reservation.application.exceptions.ConflictException;
import com.planet.reservation.application.exceptions.NotFoundException;
import com.planet.reservation.application.exceptions.UnauthorizedException;
import com.planet.reservation.application.exceptions.UnprocessableException;
import com.planet.reservation.domain.dto.response.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class CustomExceptionHandler {
    private static final Logger log = LogManager.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(value = { Exception.class })
    public final ResponseEntity<Object> handleException(Exception ex) {
        log.error("Exception has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = { UnprocessableException.class })
    public final ResponseEntity<Object> handleUnprocessableException(UnprocessableException ex) {
        log.error("UnprocessableException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = { IllegalStateException.class })
    public final ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = { NotFoundException.class })
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        log.error("NotFoundException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UnauthorizedException.class })
    public final ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("UnauthorizedException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { ConflictException.class })
    public final ResponseEntity<Object> handleConflictException(ConflictException ex) {
        log.error("ConflictException has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = { HttpServerErrorException.InternalServerError.class })
    public final ResponseEntity<Object> handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
        log.error("InternalServerError has occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
