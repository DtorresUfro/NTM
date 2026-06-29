package com.ntm.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendar")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_calendar_id")
    private String googleCalendarId;

    private String name;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks =
            new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes =
            new ArrayList<>();


    public Calendar() {}


    public Calendar(String name, String googleCalendarId) {
        this.name = name;
        this.googleCalendarId =
                googleCalendarId;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }


    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId =
                googleCalendarId;
    }


    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }


    public List<Task> getTasks() {return tasks;}


    public void setTasks(List<Task> tasks) {this.tasks = tasks;
    }

    public List<Note> getNotes() {return notes;}

    public void setNotes(List<Note> notes) {this.notes = notes;}

    public void addTask(Task task) {
        tasks.add(task);
        task.setCalendar(this);
    }

    public void addNote(Note note) {
        notes.add(note);
        note.setCalendar(this);
    }
}

