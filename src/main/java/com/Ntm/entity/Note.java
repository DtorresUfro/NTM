package com.Ntm.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_master_key", nullable = false)
    private String roomMasterKey;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean active = true;

    public Note(){}

    public Note(String title, String content, String createdBy, String roomMasterKey) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.roomMasterKey = roomMasterKey;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {return id;}
    public String getRoomMasterKey() {return roomMasterKey;}
    public String getTitle() {return title;}
    public String getContent() {return content;}
    public String getCreatedBy() {return createdBy;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public boolean isActive() {return active;}

    public void setId(Long id) {this.id = id;}
    public void setRoomMasterKey(String roomMasterKey) {this.roomMasterKey = roomMasterKey;}
    public void setTitle(String title) {this.title = title;}
    public void setContent(String content) {this.content = content;}
    public void setCreatedBy(String createdBy) {this.createdBy = createdBy;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}
    public void setActive(boolean active) {this.active = active;}

    public void editContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
        System.out.println("Contenido actualizado.");
    }

    public void delete() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "Note{" +
                "\nid =" + id +
                "\ntítulo ='" + title + '\'' +
                "\ncontenido ='" + content + '\'' +
                "\ncreado por: ='" + createdBy + '\'' +
                "\ncreado a las =" + createdAt +
                "\nactualizado a las =" + updatedAt +
                "\nactivo =" + active +
                '}';
    }
}