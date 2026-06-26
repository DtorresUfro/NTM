package com.Ntm.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendars")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tareas = new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notas = new ArrayList<>();

    public Calendar() {}

    public void addNote(Note nota) {
        notas.add(nota);
        nota.setCalendar(this);
    }

    public void addTask(Task tarea) {
        tareas.add(tarea);
        tarea.setCalendar(this);
    }

    public List<Task> getTasks() {
        return this.tareas;
    }

    public List<Note> getNotes() {
        return this.notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}