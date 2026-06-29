package com.ntm.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Sala donde pertenece la notificación
    @Column(nullable = false)
    private String roomMasterKey;


    // Usuario que recibe la notificación
    private String targetUser;


    // Mensaje mostrado en el buzón
    @Column(columnDefinition = "TEXT")
    private String message;


    // Si el usuario ya la vio
    private boolean readStatus = false;


    // Fecha de creación
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();



    public Notification() {
    }



    public Notification(
            String roomMasterKey,
            String targetUser,
            String message
    ){
        this.roomMasterKey = roomMasterKey;
        this.targetUser = targetUser;
        this.message = message;
        this.createdAt = new Date();
        this.readStatus = false;
    }



    public Long getId() {
        return id;
    }


    public String getRoomMasterKey() {
        return roomMasterKey;
    }


    public void setRoomMasterKey(String roomMasterKey) {
        this.roomMasterKey = roomMasterKey;
    }


    public String getTargetUser() {
        return targetUser;
    }


    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isReadStatus() {
        return readStatus;
    }


    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }


    public Date getCreatedAt() {
        return createdAt;
    }


    public void markAsRead(){

        this.readStatus = true;

    }

}