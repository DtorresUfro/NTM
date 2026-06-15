package com.Ntm.dto;
import java.util.List;

public class JoinRoomResponse {
    private String roomId;
    private String roomName;
    private List<String> participants;

    public JoinRoomResponse(String roomId, String roomName, List<String> participants) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.participants = participants;
    }

    public String getRoomId() {
        return roomId;
    }
    public String getRoomName() {
        return roomName;
    }
    public List<String> getParticipants() {
        return participants;
    }
}