package com.Ntm.TestUnitarios.exception;

public class UnauthorizedRoomActionException extends RuntimeException {
    public UnauthorizedRoomActionException(String message) {
        super(message);
    }
}