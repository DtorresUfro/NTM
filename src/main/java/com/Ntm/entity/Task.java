package com.Ntm.entity;
import java.util.*;

public class Task {
    private String title;
    private String description;
    private Date dueDate;
    private String createdBy;
    private Date createdAt;
    private boolean completed;

    public Task(String title, String description, Date dueDate, String createdBy, Date createdAt, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.completed = completed;
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
