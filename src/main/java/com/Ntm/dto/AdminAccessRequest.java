package com.Ntm.dto;

public class AdminAccessRequest {
    private String roomId;
    private String masterKey;
    private String userName;

    // Constructor vacío (Necesario para que Spring serialice el JSON)
    public AdminAccessRequest() {
    }

    public AdminAccessRequest(String roomId, String masterKey, String userName) {
        this.roomId = roomId;
        this.masterKey = masterKey;
        this.userName = userName;
    }


    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getMasterKey() { return masterKey; }
    public void setMasterKey(String masterKey) { this.masterKey = masterKey; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}