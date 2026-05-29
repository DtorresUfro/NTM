package com.Ntm.entity;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "task")
public class Task {

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

    public Task(String title, String description, Date dueDate, String createdBy, String roomMasterKey) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.roomMasterKey = roomMasterKey;
        this.createdAt = new Date();
        this.completed = false;
    }

    public void edit(String title, String description, Date dueDate){

    }

    public void complete(){
        this.completed = true;
    }

    public boolean isOverdue(){


        return false;
    }

    public void delete(){

    }
}
