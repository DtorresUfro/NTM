package com.Ntm.dto;

public class CreateRoomRequest {
    private String roomName;
    private String adminName;

    public String getRoomName() {
        return roomName;
    }
    public String getAdminName() {
        return adminName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}