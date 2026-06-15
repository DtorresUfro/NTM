package com.Ntm.TestUnitarios.exception;

public class InvalidRoomException extends RuntimeException {
    public InvalidRoomException(String message) {
        super(message);
    }
}