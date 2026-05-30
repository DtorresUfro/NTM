package com.Ntm.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Room {
    @Id
    private String masterKey;
    private String name;
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private String adminName;
    @ElementCollection
    private List<String> participants = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    public Room() {
    }
    public Room(String name, String adminName) {
        this.name = name;
        this.adminName = adminName;
        this.id = generateJoinId();
        this.masterKey = generateMasterKey();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.participants.add(adminName);
    }

    public String generateJoinId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    public String generateMasterKey() {
        return "MK-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public String getId() { return id; }
    public String getMasterKey() { return masterKey; }
    public String getName() { return name; }
    public List<String> getParticipants() { return participants; }
    public String getAdminName(){
        return adminName;
    }

    public void addParticipant(String name) {
        this.participants.add(name);
        this.lastActivity = LocalDateTime.now();
    }
    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}