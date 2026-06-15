package com.Ntm.exception;

public class UnauthorizedRoomActionException extends RuntimeException {
    public UnauthorizedRoomActionException(String message) {
        super(message);
    }
}