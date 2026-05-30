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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
