package com.codexist.pinpoint.exception;

public class PlaceAlreadySavedException extends RuntimeException {
    public PlaceAlreadySavedException(String message) {
        super(message);
    }
}