package com.ntm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;

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

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "due_date", nullable = false)
    private Date dueDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(nullable = false)
    private boolean active = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;


    public Task() {
    }

    public Task(String title, String description, Date startDate, Date dueDate, String createdBy) {

        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.createdBy = createdBy;

        this.createdAt = new Date();
        this.completed = false;
        this.active = true;

    }

    public Task(String title, String description, Date dueDate, String createdBy, Date createdAt, boolean completed) {
        this.title = title;
        this.description = description;
        this.startDate = createdAt;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.completed = completed;
        this.active = true;
    }

    public void edit(String title, String description, Date dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public void complete() {
        this.completed = true;
    }


    public boolean isOverdue() {

        if (completed) {
            return false;
        }


        if (dueDate == null) {
            return false;
        }


        return dueDate.before(new Date());

    }

    public void delete() {
        this.active = false;
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

    public void setId(Long id) {
        this.id = id;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
