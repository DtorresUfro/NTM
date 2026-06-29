package com.ntm.dto;

public class RoomMemberResponse {
    private String username;
    private boolean admin;
    private boolean connected;

    public RoomMemberResponse() {}

    public RoomMemberResponse(String username, boolean admin, boolean connected) {
        this.username = username;
        this.admin = admin;
        this.connected = connected;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
