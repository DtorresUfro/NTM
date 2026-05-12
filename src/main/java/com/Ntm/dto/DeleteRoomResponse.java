package com.Ntm.dto;

public class DeleteRoomResponse {
    private String message;

    public DeleteRoomResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}