package com.Ntm.exception;

public class InvalidMasterKeyException extends RuntimeException {
    public InvalidMasterKeyException(String message) {
        super(message);
    }
}