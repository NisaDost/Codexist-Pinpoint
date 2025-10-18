package com.codexist.pinpoint.exception;

public class GooglePlacesApiException extends RuntimeException {
    public GooglePlacesApiException(String message) {
        super(message);
    }

    public GooglePlacesApiException(String message, Throwable cause) {
        super(message, cause);
    }
}