package com.Ntm.dto;

public class AdminAccessResponse {
    private String roomId;
    private String role;

    public AdminAccessResponse() {
    }

    public AdminAccessResponse(String roomId, String role) {
        this.roomId = roomId;
        this.role = role;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}