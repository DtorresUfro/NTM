package com.Ntm.entity;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "task")
public class Task {

    //Variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_master_key", nullable = false)
    private String roomMasterKey;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(nullable = false)
    private boolean completed;
    private boolean active = true;

    //Constructor
    public Task() {}

    public Task(String title, String description, Date dueDate, String createdBy, String roomMasterKey) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.roomMasterKey = roomMasterKey;
        this.createdAt = new Date();
        this.completed = false;
    }

    public Long getId() {return id;}
    public String getRoomMasterKey() {return roomMasterKey;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public Date getDueDate() {return dueDate;}
    public String getCreatedBy() {return createdBy;}
    public Date getCreatedAt() {return createdAt;}
    public boolean isCompleted() {return completed;}
    public boolean isActive() {return active;}

    public void setId(Long id) {this.id = id;}
    public void setRoomMasterKey(String roomMasterKey) {this.roomMasterKey = roomMasterKey;}
    public void setTitle(String title) {this.title = title;}
    public void setDescription(String description) {this.description = description;}
    public void setDueDate(Date dueDate) {this.dueDate = dueDate;}
    public void setCreatedBy(String createdBy) {this.createdBy = createdBy;}
    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}
    public void setCompleted(boolean completed) {this.completed = completed;}
    public void setActive(boolean active) {this.active = active;}

    //Métodos
    public void edit(String title, String description, Date dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        System.out.println("Contenido actualizado.");
    }

    public void complete(){
        this.completed = true;
        System.out.println("Tarea completada");
    }

    public boolean isOverdue() {
        Date now = new Date();
        System.out.println("Esta tarea está atrasada.");
        return !this.completed && this.dueDate != null && this.dueDate.before(now);
    }

    public void delete(){ this.active = false; }

    @Override
    public String toString() {
        return "Task{" +
                "\nid =" + id +
                "\ntítulo ='" + title + '\'' +
                "\ndescripción ='" + description + '\'' +
                "\nfecha de término =" + dueDate +
                "\ncompletado =" + completed +
                "\nactivo =" + active +
                '}';
    }
}