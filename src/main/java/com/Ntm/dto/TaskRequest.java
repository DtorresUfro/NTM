package com.Ntm.dto;

import java.util.Date;

public class TaskRequest {
    private Long id;
    private String roomId;
    private String username;
    private String taskTitle;
    private String description;
    private Date startDate;
    private Date dueDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
}