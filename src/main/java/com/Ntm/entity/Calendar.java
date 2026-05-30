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

    @Transient
    private List<Task> tareas = new ArrayList<>();

    @Transient
    private List<Note> notas = new ArrayList<>();

    public Calendar() {}

    public void addTask(Task tarea) {
        this.tareas.add(tarea);
    }

    public void addNote(Note nota) {
        this.notas.add(nota);
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