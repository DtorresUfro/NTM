package com.Ntm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "room_master_key",
            nullable = false
    )
    private String roomMasterKey;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(
            name = "created_by",
            nullable = false
    )
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    public Note() {}

    public Note(String title, String content, String createdBy) {

        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void editContent(
            String newContent
    ) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();}

    public void delete() {this.active = false;}

    public Long getId() {return id;}

    public String getRoomMasterKey() {return roomMasterKey;}


    public void setRoomMasterKey(String roomMasterKey) {
        this.roomMasterKey = roomMasterKey;
    }


    public String getTitle() {return title;}


    public void setTitle(String title) {this.title = title;
    }


    public String getContent() {return content;}


    public void setContent(String content) {
        this.content = content;
    }


    public String getCreatedBy() {return createdBy;}


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }


    public LocalDateTime getCreatedAt() {return createdAt;}


    public LocalDateTime getUpdatedAt() {return updatedAt;}


    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;
    }


    public boolean isActive() {return active;}

    public void setActive(boolean active
    ) {this.active = active;
    }

    public Calendar getCalendar() {return calendar;}

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}

