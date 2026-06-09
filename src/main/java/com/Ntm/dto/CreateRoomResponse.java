package com.Ntm.dto;

public class CreateRoomResponse {
    private String roomId;
    private String masterKey;
    private String roomName;

    public CreateRoomResponse(String roomId, String masterKey, String roomName) {
        this.roomId = roomId;
        this.masterKey = masterKey;
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }
    public String getMasterKey() {
        return masterKey;
    }
    public String getRoomName() {
        return roomName;
    }
}