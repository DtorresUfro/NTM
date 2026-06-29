package com.ntm.exception;

public class UnauthorizedRoomActionException extends RuntimeException {
    public UnauthorizedRoomActionException(String message) {
        super(message);
    }
}