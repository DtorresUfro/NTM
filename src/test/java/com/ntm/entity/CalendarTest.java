package com.ntm.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {
    @Test
    void shouldManageIdCorrectly() {
        Calendar calendar = new Calendar();

        calendar.setId(100L);

        assertEquals(100L, calendar.getId());
    }

    @Test
    void shouldUseGettersAndSettersCorrectly() {

        Calendar calendar = new Calendar();

        Long id = 1L;
        String name = "Calendario Sala";
        String googleId = "google-123";

        List<Task> tasks = new ArrayList<>();
        List<Note> notes = new ArrayList<>();

        calendar.setId(id);
        calendar.setName(name);
        calendar.setGoogleCalendarId(googleId);
        calendar.setTasks(tasks);
        calendar.setNotes(notes);

        assertEquals(id, calendar.getId());
        assertEquals(name, calendar.getName());
        assertEquals(googleId, calendar.getGoogleCalendarId());
        assertSame(tasks, calendar.getTasks());
        assertSame(notes, calendar.getNotes());
    }

    @Test
    void shouldCreateCalendarUsingConstructor() {

        Calendar calendar = new Calendar("Sala de Estudio", "google-123");

        assertEquals("Sala de Estudio", calendar.getName());
        assertEquals("google-123", calendar.getGoogleCalendarId());
    }

    @Test
    void shouldAddTask() {
        Calendar calendar = new Calendar();
        Task task = new Task();
        calendar.addTask(task);

        assertEquals(1, calendar.getTasks().size());
        assertSame(calendar, task.getCalendar());
    }

    @Test
    void shouldAddNoteToCalendar() {
        Calendar calendar = new Calendar();
        Note note = new Note();
        calendar.addNote(note);

        assertEquals(1, calendar.getNotes().size());
        assertTrue(calendar.getNotes().contains(note));
        assertSame(calendar, note.getCalendar());
    }
}
