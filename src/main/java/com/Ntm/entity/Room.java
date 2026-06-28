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
    @CollectionTable(
            name = "room_participants",
            joinColumns = @JoinColumn(name = "room_master_key")
    )
    @Column(name = "participant_name")
    private List<String> participants = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "room_disconnected_participants",
            joinColumns = @JoinColumn(name = "room_master_key")
    )
    @Column(name = "participant_name")
    private List<String> disconnectedParticipants = new ArrayList<>();

    @Column(name = "google_calendar_id")
    private String googleCalendarId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)   // ← SOLO @OneToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    public Room() {}

    public Room(String name, String adminName) {
        this.name = name;
        this.adminName = adminName;
        this.id = generateJoinId();
        this.masterKey = generateMasterKey();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.participants.add(adminName);
        this.googleCalendarId = null;
    }

    public String generateJoinId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String generateMasterKey() {
        return "MK-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getMasterKey() { return masterKey; }
    public String getName() { return name; }
    public List<String> getParticipants() { return participants; }
    public List<String> getDisconnectedParticipants() { return disconnectedParticipants; }
    public String getAdminName() { return adminName; }
    public String getGoogleCalendarId() { return googleCalendarId; }
    public Calendar getCalendar() { return calendar; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMasterKey(String masterKey) { this.masterKey = masterKey; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    public void setGoogleCalendarId(String googleCalendarId) { this.googleCalendarId = googleCalendarId; }
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }
    public void setDisconnectedParticipants(List<String> disconnectedParticipants) { this.disconnectedParticipants = disconnectedParticipants; }

    public void addParticipant(String name) {
        this.participants.add(name);
        this.lastActivity = LocalDateTime.now();
    }
}