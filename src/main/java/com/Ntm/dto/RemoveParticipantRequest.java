package com.Ntm.dto;

public class RemoveParticipantRequest {
    private String roomId;
    private String adminName;
    private String usernameToRemove;

    public String getRoomId() {
        return roomId;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getUsernameToRemove() {
        return usernameToRemove;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setUsernameToRemove(String usernameToRemove) {
        this.usernameToRemove = usernameToRemove;
    }
}