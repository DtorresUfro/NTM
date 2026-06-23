package com.Ntm.dto;

public class JoinRoomRequest {
    private String roomId;
    private String username;

    public String getRoomId() {
        return roomId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}