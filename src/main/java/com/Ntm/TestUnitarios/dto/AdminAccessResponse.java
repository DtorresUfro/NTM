package com.Ntm.TestUnitarios.dto;

public class AdminAccessResponse {
    private String roomId;
    private String adminName;

    public AdminAccessResponse() {
    }

    public AdminAccessResponse(String roomId, String adminName) {
        this.roomId = roomId;
        this.adminName = adminName;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
}