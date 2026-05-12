package com.Ntm;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
    private String name;
    private String masterKey;
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private List<String> participants;
    private String adminName;

    public Room(String name, String adminName) {
        this.name = name;
        this.adminName = adminName;
        this.id = generateJoinId();
        this.masterKey = generateMasterKey();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.participants = new ArrayList<>();
        this.participants.add(adminName);
    }

    public String generateJoinId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generateMasterKey() {
        return "MK-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public void setName(String name) { this.name = name; }
    public void setMasterKey(String masterKey) { this.masterKey = masterKey; }
    public void setId(String id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    public void setAdminName(String adminName) { this.adminName = adminName; }


    public String getId() { return id; }
    public String getMasterKey() { return masterKey; }
    public String getName() { return name; }
    public List<String> getParticipants() { return participants; }

    public void addParticipant(String name) {
        this.participants.add(name);
        this.lastActivity = LocalDateTime.now();
    }
}