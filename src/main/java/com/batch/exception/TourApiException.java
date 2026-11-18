package com.batch.exception;

public class TourApiException extends RuntimeException {
    public TourApiException(String message) {
        super(message);
    }
}